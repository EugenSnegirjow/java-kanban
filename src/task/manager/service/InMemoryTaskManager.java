package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int taskId = 0;
    private static HashMap<Integer, Task> tasks = new HashMap<>();
    private static HashMap<Integer, Epic> epics = new HashMap<>();
    private static HashMap<Integer, SubTask> subTasks = new HashMap<>();
    public static Task getTask(int taskId) {
        return tasks.get(taskId);
    }
    public static Epic getEpic(int taskId) {
        return epics.get(taskId);
    }
    public static SubTask getSubTask(int taskId) {
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
        for (Integer task : InMemoryTaskManager.tasks.keySet()) {
            tasks.add(InMemoryTaskManager.tasks.get(task));
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
    public void removeTask(Integer taskID) {
        tasks.remove(taskID);
    }

    @Override
    public void removeEpic(Integer taskId) {
        for (Integer subTaskId : epics.get(taskId).getSubTaskIds()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(taskId);
    }

    @Override
    public void removeSubTask(Integer taskId) {
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
