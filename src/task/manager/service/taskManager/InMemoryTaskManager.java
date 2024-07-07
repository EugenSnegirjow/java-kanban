package task.manager.service.taskManager;

import task.manager.enums.Status;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.Managers;
import task.manager.service.exception.NotFoundException;
import task.manager.service.exception.WrongTaskException;
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
        if (tasks.size() == 0) throw new NotFoundException("Список задач пуст");
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new NotFoundException("Задачи с ID " + taskId + " не существует");
        }
        managerHistory.add(tasks.get(taskId));
        return task;
    }

    public Epic getEpic(Integer taskId) {
        if (epics.size() == 0) throw new NotFoundException("Список эпиков пуст");
        Epic epic = epics.get(taskId);
        if (epic == null) {
            throw new NotFoundException("Эпика с ID " + taskId + " не существует");
        }
        managerHistory.add(epics.get(taskId));
        return epic;
    }

    public SubTask getSubTask(Integer taskId) {
        if (subTasks.size() == 0) throw new NotFoundException("Список подзадач пуст");
        SubTask subTask = subTasks.get(taskId);
        if (subTask == null) {
            throw new NotFoundException("Подзадачи с ID " + taskId + " не существует");
        }
        managerHistory.add(subTasks.get(taskId));
        return subTask;
    }

    @Override
    public int create(Task task) {
        task.setId(taskId);
        if (isHasIntersection(task)) {
            throw new WrongTaskException("Обнаружено пересечение времени выполнения задачи. " +
                    "Нельзя выполнять несколько задач одновременна. Задача не добавлена");
        }
        sortedTasksAndSubTasks.add(task);
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

        try {
            if (isHasIntersection(subTask)) {
                throw new WrongTaskException("Обнаружено пересечение времени выполнения задачи. " +
                        "Нельзя выполнять несколько задач одновременно. Подзадача не добавлена");
            }
            sortedTasksAndSubTasks.add(subTask);

            if (!epics.containsKey(epic)) {
                throw new NotFoundException("Эпика с ID " + epic + " не существует. Подзадача не добавлена");
            }
            sortedSubTasks.add(subTask);
            subTasks.put(subTask.getId(), subTask);
            if (!epics.get(epic).getSubTaskIds().contains(subTask.getId())) {
                epics.get(epic).addSubTaskId(subTask.getId());
            }
            updateEpicTime(epics.get(epic));
            updateEpicStatusForCreateSubTask(epics.get(epic), subTask.getStatus());
            taskId++;

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            subTask.setId(-1);
        } finally {
            return subTask.getId();
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
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
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
        if (!tasks.containsKey(taskId)) {
            throw new NotFoundException("Задачи с id " + taskId + " не существует. Задача не была удалена");
        }
        sortedTasks.remove(tasks.get(taskId));
        sortedTasksAndSubTasks.remove(tasks.get(taskId));
        managerHistory.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void removeEpic(Integer epicId) {
        if (!epics.containsKey(epicId)) {
            throw new NotFoundException("Эпика с id " + epicId + " не существует. Задача не была удалена");
        }
        managerHistory.remove(epicId);
        ArrayList<Integer> subtasksIdsList = epics.get(epicId).getSubTaskIds();
        for (Integer subTaskId : subtasksIdsList) {
            managerHistory.remove(subTaskId);

            SubTask subTask = subTasks.get(subTaskId);
            System.out.println(sortedSubTasks.contains(subTask));
            sortedSubTasks.remove(subTask);
            sortedTasksAndSubTasks.remove(subTask);

            subTasks.remove(subTaskId);
        }
        epics.remove(epicId);
    }

    @Override
    public void removeSubTask(Integer taskId) {
        if (!subTasks.containsKey(taskId)) {
            throw new NotFoundException("Подзадачи с id " + taskId + " не существует. Задача не была удалена");
        }
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
        if (!tasks.containsKey(task.getId())) {
            throw new NotFoundException("Задача с id " + task.getId() + " не существует, обновление задачи не выполнено");
        }
        if (!sortedTasksAndSubTasks.add(task)) {
            throw new WrongTaskException("Задача с началом в " + task.getStartTime() + " уже существует. " +
                    "Не может быть нескольких задач с одинаковым временем начала.");
        }
        sortedTasks.add(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void update(Epic newEpic) {
        if (!epics.containsKey(newEpic.getId())) {
            throw new NotFoundException("Эпика с id " + newEpic.getId() + " не существует, обновление эпика не выполнено");
        }
        Epic oldEpic = epics.get(newEpic.getId());
        oldEpic.getSubTaskIds().stream().peek(newEpic::addSubTaskId).close();
        epics.put(newEpic.getId(), newEpic);
    }

    @Override
    public void update(SubTask task) {
        if (!subTasks.containsKey(task.getId())) {
            throw new NotFoundException("Подзадачи с id " + task.getId() + " не существует, обновление подзадачи не выполнено");
        }
//        sortedTasksAndSubTasks.remove(subTasks.get(task.getId()));
//        sortedSubTasks.remove(subTasks.get(task.getId()));

        if (!sortedTasksAndSubTasks.add(task)) {
            throw new WrongTaskException("Задача с началом в " + task.getStartTime() + " уже существует. " +
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

        // Сделать duration расчетной
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
                .filter(Objects::nonNull)
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

    public Boolean isHasIntersection(Task newTask) {
        if (newTask.getStartTime() == null) return false;

        LocalDateTime start;
        LocalDateTime end;
        LocalDateTime startNew = newTask.getStartTime();
        LocalDateTime endNew = newTask.getEndTime();

        for (Task createdTask : sortedTasksAndSubTasks) {
            start = createdTask.getStartTime();
            end = createdTask.getEndTime();

            if (start == null) continue;
            if ((startNew.isAfter(start) && startNew.isBefore(end))
                    || endNew.isAfter(start) && endNew.isBefore(end)
                    || (startNew.isEqual(start) || endNew.isEqual(end)))
                return true;
        }
        return false;
    }

    private int onTimeComparator(Task task1, Task task2) {
        if (task1.getStartTime() == null) return -1;
        if (task2.getStartTime() == null) return 1;
        if (task1.getStartTime().isBefore(task2.getStartTime())) return -1;
        if (task1.getStartTime().isAfter(task2.getStartTime())) return 1;
        else return 0;
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
