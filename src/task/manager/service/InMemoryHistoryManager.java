package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    public ArrayList<Task> tasksHistoryList = new ArrayList<>();

    @Override
    public Task getTask(int taskID) {
        add(InMemoryTaskManager.tasks.get(taskID));
        return InMemoryTaskManager.tasks.get(taskID);
    }

    @Override
    public Epic getEpic(int taskID) {
        add(InMemoryTaskManager.epics.get(taskID));
        return InMemoryTaskManager.epics.get(taskID);
    }

    @Override
    public SubTask getSubTask(int taskID) {
        add(InMemoryTaskManager.subTasks.get(taskID));
        return InMemoryTaskManager.subTasks.get(taskID);
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
