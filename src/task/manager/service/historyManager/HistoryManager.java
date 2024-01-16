package task.manager.service.historyManager;

import task.manager.model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);
    void remove(int id);
    ArrayList<Task> getHistory();
    /** В задании написано про такой метод, но я его добавил, чтобы удалять историю при удалении всех задач в методе
     removeAllTasks() в менеджере задач*/
    void removeHistory();

}


