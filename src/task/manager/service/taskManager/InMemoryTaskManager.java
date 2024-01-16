package task.manager.service.taskManager;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.Managers;
import task.manager.service.historyManager.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private HistoryManager history = Managers.getDefaultHistory();
    private int taskId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }

    public Task getTask(int taskId) {
        history.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    public Epic getEpic(int taskId) {
        history.add(epics.get(taskId));
        return epics.get(taskId);
    }

    public SubTask getSubTask(int taskId) {
        history.add(subTasks.get(taskId));
        return subTasks.get(taskId);
    }

    @Override
    public int create(Task task) {
        taskId++;
        task.setId(taskId);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int create(Epic task) {
        taskId++;
        task.setId(taskId);
        epics.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int create(int epic, SubTask subTask) {
        subTask.setEpicTaskId(epic);
        taskId++;
        subTask.setId(taskId);
        subTasks.put(subTask.getId(), subTask);
        epics.get(epic).addSubTaskIds(subTask.getId());
        updateEpicStatusForCreateSubTask(epics.get(epic), subTask.getStatus());
        return subTask.getId();
    }

    private void updateEpicStatusForCreateSubTask(Epic epic, Status subTaskStatus) {
        switch (subTaskStatus) {
            case NEW:
                if (epic.getStatus().equals(Status.DONE)) {
                    epic.setStatus(Status.IN_PROGRESS);
                }
                break;
            case IN_PROGRESS:
                epic.setStatus(Status.IN_PROGRESS);
                break;
            case DONE:
                Status status = Status.DONE;
                for (Integer subTaskID : epic.getSubTaskIds()) {
                    if (!subTasks.get(subTaskID).getStatus().equals((status))) {
                        status = Status.IN_PROGRESS;
                        break;
                    }
                }
                epic.setStatus(status);
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
        history.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void removeEpic(Integer taskId) {
        history.remove(taskId);
        for (Integer subTaskId : epics.get(taskId).getSubTaskIds()) {
            history.remove(subTaskId);
            subTasks.remove(subTaskId);
        }
        epics.remove(taskId);
    }

    @Override
    public void removeSubTask(Integer taskId) {
        history.remove(taskId);
        int epicID = subTasks.get(taskId).getEpicTaskId();
        Status subTaskStatus = subTasks.get(taskId).getStatus();
        subTasks.remove(taskId);
        epics.get(epicID).removeSubtaskId(taskId);
        updateEpicStatusForRemoveSubTask(epics.get(epicID), subTaskStatus);
    }

    private void updateEpicStatusForRemoveSubTask(Epic epic, Status subTaskStatus) {
        if (epic.getSubTaskIds() == null) {
            epic.setStatus(Status.NEW);
        } else if (subTaskStatus.equals(Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            Status status = Status.NEW;
            for (Integer subTaskID : epic.getSubTaskIds()) {
                if (!subTasks.get(subTaskID).getStatus().equals(status)) {
                    status = Status.IN_PROGRESS;
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
    }

    @Override
    public void update(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void update(Epic task) {
        epics.put(task.getId(), task);
    }

    @Override
    public void update(SubTask task) {
        subTasks.put(task.getId(), task);
        switch (task.getStatus()) {
            case NEW:
                if (epics.get(task.getEpicTaskId()).getStatus().equals(Status.DONE)) {
                    epics.get(task.getEpicTaskId()).setStatus(Status.IN_PROGRESS);
                }
                break;
            case IN_PROGRESS:
                if (epics.get(task.getEpicTaskId()).getStatus().equals(Status.DONE)
                        || epics.get(task.getEpicTaskId()).getStatus().equals(Status.NEW)) {
                    epics.get(task.getEpicTaskId()).setStatus(Status.IN_PROGRESS);
                }
                break;
            case DONE:
                Status status = Status.DONE;
                for (Integer subTaskID : epics.get(task.getEpicTaskId()).getSubTaskIds()) {
                    if (!subTasks.get(subTaskID).getStatus().equals(status)) {
                        status = Status.IN_PROGRESS;
                    }
                }
                epics.get(task.getEpicTaskId()).setStatus(status);
        }
    }
}
