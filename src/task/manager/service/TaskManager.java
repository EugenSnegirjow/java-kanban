package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

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
        epics.get(epic).subTaskIDs.add(subTask.getID());
        switch (subTask.getStatus()) {
            case "NEW":
                if (epics.get(epic).getStatus().equals("DONE")) {
                    epics.get(epic).setStatus("IN_PROGRESS");
                }
                break;
            case "IN_PROGRESS":
                    epics.get(epic).setStatus("IN_PROGRESS");
                break;
            case "DONE":
                String status = "DONE";
                for (Integer subTaskID : epics.get(epic).subTaskIDs) {
                    if (!subTasks.get(subTaskID).equals(status)) {
                        status = "IN_PROGRESS";
                        break;
                    }
                }
                epics.get(epic).setStatus(status);
        }
        return subTask.getID();
    }

    public void printAllTasks() {
        for (Integer ID : tasks.keySet()) {
            System.out.println(tasks.get(ID));
        }
        for (Integer ID : epics.keySet()) {
            System.out.println(epics.get(ID));
        }
    }

    public void printEpicWithSubTasks(Integer taskID){
        System.out.println(epics.get(taskID));
    }

    public void removeTask(Integer taskID){
        tasks.remove(taskID);
    }
    public void removeEpic(Integer taskID){
        epics.remove(taskID);
    }
    public void removeSubTask(Integer taskID){
        int epicID = subTasks.get(taskID).getEpicTaskID();
        String subTaskStatus = subTasks.get(taskID).getStatus();
        subTasks.remove(taskID);
        if (epics.get(epicID).subTaskIDs == null){
            epics.get(epicID).setStatus("NEW");
        } else if (subTaskStatus.equals("DONE")) {
            epics.get(epicID).setStatus("DONE");
        } else {
            String status = "NEW";
            for (Integer subTaskID : epics.get(epicID).subTaskIDs) {
                if (!subTasks.get(subTaskID).equals(status)) {
                    status = "IN_PROGRESS";
                    break;
                }
            }
            epics.get(epicID).setStatus(status);
        }
    }
    public void removeAllTasks(){
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
                for (Integer subTaskID : epics.get(task.getEpicTaskID()).subTaskIDs) {
                    if (!subTasks.get(subTaskID).equals(status)) {
                        status = "IN_PROGRESS";
                    }
                }
                epics.get(task.getEpicTaskID()).setStatus(status);
        }
    }

}
