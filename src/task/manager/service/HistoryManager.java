package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    Task getTask(int taskID);

    Epic getEpic(int taskID);

    SubTask getSubTask(int taskID);

    void add(Task task);

    ArrayList<Task> getHistory();
}
