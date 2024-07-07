package task.manager.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import task.manager.service.historyManager.HistoryManager;
import task.manager.service.historyManager.InMemoryHistoryManager;
import task.manager.service.taskManager.InMemoryTaskManager;
import task.manager.service.taskManager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter()).create();
    }
}
