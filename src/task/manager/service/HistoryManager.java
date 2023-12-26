package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.util.ArrayList;

public interface HistoryManager <T extends Task> {
    Task getTask(int taskID);
    Epic getEpic(int taskID);
    SubTask getSubTask (int taskID);

    void add(T task);
    ArrayList<T> getHistory();
}
