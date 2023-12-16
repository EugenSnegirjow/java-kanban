package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskID = 0;
    public HashMap<Integer, Task> tasks;
    public HashMap<Integer, Epic> epics;
    public HashMap<Integer, SubTask> subTasks;

    public int create(Task task) {
        taskID++;
        task.setID(taskID);
        tasks.put(task.getID(), task);
        return task.getID();
    }

    public int create(Epic task) {
        taskID++;
        task.setID(taskID);
        epics.put(task.getID(), task);
        return task.getID();
    }

    public int create(int epic, SubTask subTask) {
        taskID++;
        subTask.setID(taskID);
        subTasks.put(subTask.getID(), subTask);
        epics.get(epic).addSubTaskIDs(subTask.getID());
        updateEpicStatusForCreateSubTask(epics.get(epic), subTask.getStatus());
        return subTask.getID();
    }

    private void updateEpicStatusForCreateSubTask(Epic epic, String subTaskStatus) {
        switch (subTaskStatus) {
            case "NEW":
                if (epic.getStatus().equals("DONE")) {
                    epic.setStatus("IN_PROGRESS");
                }
                break;
            case "IN_PROGRESS":
                epic.setStatus("IN_PROGRESS");
                break;
            case "DONE":
                String status = "DONE";
                for (Integer subTaskID : epic.getSubTaskIDs()) {
                    if (!subTasks.get(subTaskID).equals(status)) {
                        status = "IN_PROGRESS";
                        break;
                    }
                }
                epic.setStatus(status);
        }

    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Integer task : this.tasks.keySet()) {
            tasks.add(this.tasks.get(task));
        }
        return tasks;
    }

    public ArrayList<Task> getAllEpics() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Integer task : epics.keySet()) {
            tasks.add(epics.get(task));
        }
        return tasks;
    }

    public ArrayList<Task> getAllSubTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Integer task : subTasks.keySet()) {
            tasks.add(subTasks.get(task));
        }
        return tasks;
    }

    public ArrayList<Task> getAllSubTasksForEpic(Epic epic) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Integer subTaskID : epic.getSubTaskIDs()) {
            tasks.add(subTasks.get(subTaskID));
        }
        return tasks;
    }

    public void removeTask(Integer taskID) {
        tasks.remove(taskID);
    }

    public void removeEpic(Integer taskID) {
        epics.remove(taskID);
    }

    public void removeSubTask(Integer taskID) {
        int epicID = subTasks.get(taskID).getEpicTaskID();
        String subTaskStatus = subTasks.get(taskID).getStatus();
        subTasks.remove(taskID);
        epics.get(epicID).removeSubtaskID(taskID);
        updateEpicStatusForRemoveSubTask(epics.get(epicID), subTaskStatus);
    }

    private void updateEpicStatusForRemoveSubTask(Epic epic, String subTaskStatus) {
        if (epic.getSubTaskIDs() == null) {
            epic.setStatus("NEW");
        } else if (subTaskStatus.equals("DONE")) {
            epic.setStatus("DONE");
        } else {
            String status = "NEW";
            for (Integer subTaskID : epic.getSubTaskIDs()) {
                if (!subTasks.get(subTaskID).equals(status)) {
                    status = "IN_PROGRESS";
                    break;
                }
            }
            epic.setStatus(status);
        }
    }

    public void removeAllTasks() {
        tasks.clear();
        subTasks.clear();
        epics.clear();
    }

    public void update(Task task) {
        tasks.put(task.getID(), task);
    }

    public void update(Epic task) {
        epics.put(task.getID(), task);
    }

    public void update(SubTask task) {
        subTasks.put(task.getID(), task);
        switch (task.getStatus()) {
            case "NEW":
                if (epics.get(task.getEpicTaskID()).getStatus().equals("DONE")) {
                    epics.get(task.getEpicTaskID()).setStatus("IN_PROGRESS");
                }
                break;
            case "IN_PROGRESS":
                if (epics.get(task.getEpicTaskID()).getStatus().equals("DONE")
                        || epics.get(task.getEpicTaskID()).getStatus().equals("NEW")) {
                    epics.get(task.getEpicTaskID()).setStatus("IN_PROGRESS");
                }
                break;
            case "DONE":
                String status = "DONE";
                for (Integer subTaskID : epics.get(task.getEpicTaskID()).getSubTaskIDs()) {
                    if (!subTasks.get(subTaskID).equals(status)) {
                        status = "IN_PROGRESS";
                    }
                }
                epics.get(task.getEpicTaskID()).setStatus(status);
        }
    }

}
