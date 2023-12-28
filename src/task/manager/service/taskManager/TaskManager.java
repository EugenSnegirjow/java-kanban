package task.manager.service.taskManager;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getHistory();

    Task getTask(int taskID);
    Epic getEpic(int taskID);
    SubTask getSubTask(int taskID);

    int create(Task task);
    int create(Epic task);
    int create(int epic, SubTask subTask);

    ArrayList<Task> getAllTasks();
    ArrayList<Task> getAllEpics();
    ArrayList<Task> getAllSubTasks();
    ArrayList<Task> getAllSubTasksForEpic(Epic epic);

    void removeTask(Integer taskID);
    void removeEpic(Integer taskID);
    void removeSubTask(Integer taskID);
    void removeAllTasks();

    void update(Task task);
    void update(Epic task);
    void update(SubTask task);


}

