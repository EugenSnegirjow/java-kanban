package task.manager.service;

import task.manager.service.historyManager.HistoryManager;
import task.manager.service.historyManager.InMemoryHistoryManager;
import task.manager.service.taskManager.InMemoryTaskManager;
import task.manager.service.taskManager.TaskManager;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
