package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int taskID = 0;
    public static HashMap<Integer, Task> tasks = new HashMap<>();
    public static HashMap<Integer, Epic> epics = new HashMap<>();
    public static HashMap<Integer, SubTask> subTasks = new HashMap<>();

    @Override
    public int create(Task task) {
        taskID++;
        task.setID(taskID);
        tasks.put(task.getID(), task);
        return task.getID();
    }

    @Override
    public int create(Epic task) {
        taskID++;
        task.setID(taskID);
        epics.put(task.getID(), task);
        return task.getID();
    }

    @Override
    public int create(int epic, SubTask subTask) {
        taskID++;
        subTask.setID(taskID);
        subTasks.put(subTask.getID(), subTask);
        epics.get(epic).addSubTaskIDs(subTask.getID());
        updateEpicStatusForCreateSubTask(epics.get(epic), subTask.getStatus());
        return subTask.getID();
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
                for (Integer subTaskID : epic.getSubTaskIDs()) {
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
        for (Integer subTaskID : epic.getSubTaskIDs()) {
            tasks.add(subTasks.get(subTaskID));
        }
        return tasks;
    }

    @Override
    public void removeTask(Integer taskID) {
        tasks.remove(taskID);
    }

    @Override
    public void removeEpic(Integer taskID) {
        epics.remove(taskID);
    }

    @Override
    public void removeSubTask(Integer taskID) {
        int epicID = subTasks.get(taskID).getEpicTaskID();
        Status subTaskStatus = subTasks.get(taskID).getStatus();
        subTasks.remove(taskID);
        epics.get(epicID).removeSubtaskID(taskID);
        updateEpicStatusForRemoveSubTask(epics.get(epicID), subTaskStatus);
    }

    private void updateEpicStatusForRemoveSubTask(Epic epic, Status subTaskStatus) {
        if (epic.getSubTaskIDs() == null) {
            epic.setStatus(Status.NEW);
        } else if (subTaskStatus.equals(Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            Status status = Status.NEW;
            for (Integer subTaskID : epic.getSubTaskIDs()) {
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
        tasks.put(task.getID(), task);
    }

    @Override
    public void update(Epic task) {
        epics.put(task.getID(), task);
    }

    @Override
    public void update(SubTask task) {
        subTasks.put(task.getID(), task);
        switch (task.getStatus()) {
            case NEW:
                if (epics.get(task.getEpicTaskID()).getStatus().equals(Status.DONE)) {
                    epics.get(task.getEpicTaskID()).setStatus(Status.IN_PROGRESS);
                }
                break;
            case IN_PROGRESS:
                if (epics.get(task.getEpicTaskID()).getStatus().equals(Status.DONE)
                        || epics.get(task.getEpicTaskID()).getStatus().equals(Status.NEW)) {
                    epics.get(task.getEpicTaskID()).setStatus(Status.IN_PROGRESS);
                }
                break;
            case DONE:
                Status status = Status.DONE;
                for (Integer subTaskID : epics.get(task.getEpicTaskID()).getSubTaskIDs()) {
                    if (!subTasks.get(subTaskID).getStatus().equals(status)) {
                        status = Status.IN_PROGRESS;
                    }
                }
                epics.get(task.getEpicTaskID()).setStatus(status);
        }
    }
}
