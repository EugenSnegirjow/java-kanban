package task.manager.service.historyManager;

import task.manager.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> tasksHistoryList = new ArrayList<>();
    // пока не изучали LinkedList

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
