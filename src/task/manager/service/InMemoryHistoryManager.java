package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> tasksHistoryList = new ArrayList<>();

    @Override
    public Task getTask(int taskID) {
        add(InMemoryTaskManager.getTask(taskID));
        return InMemoryTaskManager.getTask(taskID);
    }

    @Override
    public Epic getEpic(int taskID) {
        add(InMemoryTaskManager.getEpic(taskID));
        return InMemoryTaskManager.getEpic(taskID);
    }

    @Override
    public SubTask getSubTask(int taskID) {
        add(InMemoryTaskManager.getSubTask(taskID));
        return InMemoryTaskManager.getSubTask(taskID);
    }

    @Override
    public void add(Task task) {
        if (tasksHistoryList.size() == 10) {
            tasksHistoryList.remove(0);
        }
        tasksHistoryList.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return tasksHistoryList;
    }
}
