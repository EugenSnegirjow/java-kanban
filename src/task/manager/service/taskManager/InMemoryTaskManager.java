package task.manager.service.taskManager;

import task.manager.enums.Status;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.Managers;
import task.manager.service.historyManager.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static task.manager.enums.Status.*;

public class InMemoryTaskManager implements TaskManager {

    private HistoryManager managerHistory;
    protected int taskId;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subTasks;

    protected TreeSet<Task> sortedTasks;
    protected TreeSet<SubTask> sortedSubTasks;
    protected TreeSet<Task> sortedTasksAndSubTasks;

    public InMemoryTaskManager() {
        managerHistory = Managers.getDefaultHistory();
        taskId = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        sortedTasks = new TreeSet<>(this::onTimeComparator);
        sortedSubTasks = new TreeSet<>(this::onTimeComparator);
        sortedTasksAndSubTasks = new TreeSet<>(this::onTimeComparator);
    }

    public HistoryManager getHistoryManager() {
        return managerHistory;
    }

    public ArrayList<Task> getManagerHistory() {
        return managerHistory.getHistory();
    }

    public Task getTask(Integer taskId) {
        if (tasks.size() == 0) return null;
        managerHistory.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    public Epic getEpic(Integer taskId) {
        if (epics.size() == 0) return null;
        managerHistory.add(epics.get(taskId));
        return epics.get(taskId);
    }

    public SubTask getSubTask(Integer taskId) {
        if (subTasks.size() == 0) return null;
        managerHistory.add(subTasks.get(taskId));
        return subTasks.get(taskId);
    }

    @Override
    public int create(Task task) {
        task.setId(taskId);
        if (!sortedTasksAndSubTasks.add(task)) {
            throw new IllegalArgumentException("Задача с началом в " + task.getStartTime() + " уже существует " +
                    "Не может быть нескольких задач с одинаковым временем начала.");
        }
        sortedTasks.add(task);
        tasks.put(task.getId(), task);
        taskId++;
        return task.getId();
    }

    @Override
    public int create(Epic task) {
        task.setId(taskId);
        epics.put(task.getId(), task);
        taskId++;
        return task.getId();
    }

    @Override
    public int create(int epic, SubTask subTask) {
        subTask.setId(taskId);
        subTask.setEpicTaskId(epic);
        if (!sortedTasksAndSubTasks.add(subTask)) {
            throw new IllegalArgumentException("Задача с началом в " + subTask.getStartTime() + " уже существует" +
                    "Не может быть нескольких задач с одинаковым временем начала.");
        }

        try {
            sortedSubTasks.add(subTask);
            subTasks.put(subTask.getId(), subTask);
            epics.get(epic).addSubTaskId(subTask.getId());
            updateEpicTime(epics.get(epic));
            updateEpicStatusForCreateSubTask(epics.get(epic), subTask.getStatus());
            taskId++;
            return subTask.getId();
        } catch (NullPointerException e) {
            throw new NullPointerException("Эпика с id " + epic + " не существует");
        }
    }

    private void updateEpicStatusForCreateSubTask(Epic epic, Status subTaskStatus) {
        if (epic.getStatus().equals(subTaskStatus)) return;
        switch (subTaskStatus) {
            case NEW:
                if (epic.getStatus().equals(DONE)) {
                    epic.setStatus(IN_PROGRESS);
                }
                break;
            case IN_PROGRESS:
                epic.setStatus(IN_PROGRESS);
                break;
            case DONE:
                Status status = DONE;
                for (Integer subTaskID : epic.getSubTaskIds()) {
                    if (!subTasks.get(subTaskID).getStatus().equals((status))) {
                        status = IN_PROGRESS;
                        epic.setStatus(status);
                        return;
                    }
                }
                epic.setStatus(status);
                break;

        }

    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Integer task : this.tasks.keySet()) {
            tasks.add(this.tasks.get(task));
        }
        return tasks;
    }

    @Override
    public ArrayList<Task> getAllEpics() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Integer task : epics.keySet()) {
            tasks.add(epics.get(task));
        }
        return tasks;
    }

    @Override
    public ArrayList<Task> getAllSubTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Integer task : subTasks.keySet()) {
            tasks.add(subTasks.get(task));
        }
        return tasks;
    }

    @Override
    public ArrayList<Task> getAllSubTasksForEpic(Epic epic) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Integer subTaskID : epic.getSubTaskIds()) {
            tasks.add(subTasks.get(subTaskID));
        }
        return tasks;
    }

    @Override
    public void removeTask(Integer taskId) {
        sortedTasks.remove(tasks.get(taskId));
        sortedTasksAndSubTasks.remove(tasks.get(taskId));
        managerHistory.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void removeEpic(Integer taskId) {
        managerHistory.remove(taskId);
        for (Integer subTaskId : epics.get(taskId).getSubTaskIds()) {
            managerHistory.remove(subTaskId);

            /** Не понимаю почему задачи не удаляются из TreeMap
             * Из-за этого не проходит проверку FileBackedTasksManagerTest*/

            System.out.println("Список сабтасков до удаления" + sortedSubTasks);
            sortedSubTasks.remove(subTasks.get(subTaskId));
            System.out.println("Список сабтасков после удаления" + sortedSubTasks);
            sortedTasksAndSubTasks.remove(subTasks.get(subTaskId));
            subTasks.remove(subTaskId);
        }
        epics.remove(taskId);
    }

    @Override
    public void removeSubTask(Integer taskId) {
        updateEpicTime(epics.get(subTasks.get(taskId).getEpicTaskId()));
        sortedSubTasks.remove(subTasks.get(taskId));
        sortedTasksAndSubTasks.remove(subTasks.get(taskId));

        managerHistory.remove(taskId);
        int epicID = subTasks.get(taskId).getEpicTaskId();
        Status subTaskStatus = subTasks.get(taskId).getStatus();
        subTasks.remove(taskId);
        epics.get(epicID).removeSubtaskId(taskId);
        updateEpicStatusForRemoveSubTask(epics.get(epicID), subTaskStatus);
    }

    private void updateEpicStatusForRemoveSubTask(Epic epic, Status subTaskStatus) {
        if (epic.getSubTaskIds() == null) {
            epic.setStatus(NEW);
        } else if (subTaskStatus.equals(DONE)) {
            epic.setStatus(DONE);
        } else {
            Status status = NEW;
            for (Integer subTaskID : epic.getSubTaskIds()) {
                if (!subTasks.get(subTaskID).getStatus().equals(status)) {
                    status = IN_PROGRESS;
                    break;
                }
            }
            epic.setStatus(status);
        }
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        subTasks.clear();
        epics.clear();
        managerHistory.removeHistory();
    }

    @Override
    public void update(Task task) {
        if (!sortedTasksAndSubTasks.add(task)) {
            throw new IllegalArgumentException("Задача с началом в " + task.getStartTime() + " уже существует" +
                    "Не может быть нескольких задач с одинаковым временем начала.");
        }
        sortedTasks.add(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void update(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());
        oldEpic.getSubTaskIds().stream().peek(newEpic::addSubTaskId).close();
        epics.put(newEpic.getId(), newEpic);
    }

    @Override
    public void update(SubTask task) {
        sortedTasksAndSubTasks.remove(subTasks.get(task.getId()));
        sortedSubTasks.remove(subTasks.get(task.getId()));

        if (!sortedTasksAndSubTasks.add(task)) {
            throw new IllegalArgumentException("Задача с началом в " + task.getStartTime() + " уже существует" +
                    "Не может быть нескольких задач с одинаковым временем начала.");
        }
        sortedSubTasks.add(task);
        updateEpicTime(epics.get(task.getEpicTaskId()));
        subTasks.put(task.getId(), task);
        switch (task.getStatus()) {
            case NEW:
                if (epics.get(task.getEpicTaskId()).getStatus().equals(DONE)) {
                    epics.get(task.getEpicTaskId()).setStatus(IN_PROGRESS);
                }
                break;
            case IN_PROGRESS:
                if (epics.get(task.getEpicTaskId()).getStatus().equals(DONE)
                        || epics.get(task.getEpicTaskId()).getStatus().equals(NEW)) {
                    epics.get(task.getEpicTaskId()).setStatus(IN_PROGRESS);
                }
                break;
            case DONE:
                Status status = DONE;
                for (Integer subTaskID : epics.get(task.getEpicTaskId()).getSubTaskIds()) {
                    if (!subTasks.get(subTaskID).getStatus().equals(status)) {
                        status = IN_PROGRESS;
                    }
                }
                epics.get(task.getEpicTaskId()).setStatus(status);
        }
    }

    private void updateEpicTime(Epic epic) {

        List<SubTask> subTaskList = sortedSubTasks.stream()
                .filter(subTask -> subTask.getEpicTaskId() == epic.getId())
                .collect(Collectors.toList());
        if (subTaskList.size() == 0) {
            epic.setStartTimeEndTimeAndDuration(null, null, null);
            return;
        }

        List<Duration> durationSubtasks = subTaskList.stream()
                .map(Task::getDuration)
                .collect(Collectors.toList());

        Duration durationEpic = Duration.ZERO;
        for (Duration durationSubtask : durationSubtasks) {
            if (durationSubtask != null) {
                durationEpic = durationEpic.plus(durationSubtask);
            }
        }
        if (durationEpic.equals(Duration.ZERO)) {
            durationEpic = null;
        }

        LocalDateTime startTime = null;
        if (subTaskList.get(0).getStartTime() != null) {
            startTime = subTaskList.get(0).getStartTime();
        }
        Optional<SubTask> subtaskWithMaxEndTime = subTaskList.stream()
                .max(this::onTimeComparator);

        SubTask subTask = subtaskWithMaxEndTime.orElse(null);

        LocalDateTime endTime = null;
        if (subTask != null) {
            endTime = subTask.getEndTime();
        }

        epic.setStartTimeEndTimeAndDuration(startTime, endTime, durationEpic);
    }

    public List<Task> getPrioritizedTasksAndSubTasks() {
        return new ArrayList<>(sortedTasksAndSubTasks);
    }

    private int onTimeComparator(Task task1, Task task2) {
        if (task1.getStartTime() == null) return -1;
        else if (task2.getStartTime() == null) return 1;
        try {
            if (task1.getStartTime().isEqual(task2.getStartTime())) return 0;
            if (task1.getStartTime().isBefore(task2.getStartTime())) return 1;
            if (task1.getStartTime().isAfter(task2.getStartTime())) return -1;
            else return 0;
        } catch (NullPointerException e) {
            return -1;
        }
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return Objects.equals(managerHistory, that.managerHistory)
                && Objects.equals(tasks, that.tasks)
                && Objects.equals(epics, that.epics)
                && Objects.equals(subTasks, that.subTasks)
                && Objects.equals(sortedTasks, that.sortedTasks)
                && Objects.equals(sortedSubTasks, that.sortedSubTasks)
                && Objects.equals(sortedTasksAndSubTasks, that.sortedTasksAndSubTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managerHistory, tasks, epics, subTasks, sortedTasks, sortedSubTasks, sortedTasksAndSubTasks);
    }
}
