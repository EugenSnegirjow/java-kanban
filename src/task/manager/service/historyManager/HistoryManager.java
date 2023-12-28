package task.manager.service.historyManager;

import task.manager.model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);

    ArrayList<Task> getHistory();
}
