package task.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.manager.enums.Status;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.taskManager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    @Override
    public void createManagers() {
        voidManager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", Status.NEW, "Description Task1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), Duration.ofMinutes(15));
        Task task2 = new Task(2, "Task2", Status.NEW, "Description Task2",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(30), Duration.ofMinutes(15));
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");
        SubTask subTask1 = new SubTask(5, "SubTask1", Status.NEW, "Description SubTask1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(60), Duration.ofMinutes(15), 4);
        SubTask subTask2 = new SubTask(6, "SubTask2", Status.NEW, "Description SubTask2",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(90), Duration.ofMinutes(15), 4);
        manager = Managers.getDefault();
        manager.create(task1);
        manager.create(task2);
        manager.create(epic1);
        manager.create(epic2);
        manager.create(epic2.getId(), subTask1);
        manager.create(epic2.getId(), subTask2);
    }

    @Test
    @Override
    public void getManagerHistory() {
        super.getManagerHistory();
    }

    @Test
    @Override
    public void getManagerHistoryForVoidHistory() {
        super.getManagerHistoryForVoidHistory();
    }

    @Test
    @Override
    void getTask() {
        super.getTask();
    }

    @Test
    @Override
    void getWrongTask() {
        super.getWrongTask();
    }

    @Test
    @Override
    void getTaskForVoidTaskList() {
        super.getTaskForVoidTaskList();
    }

    @Test
    @Override
    void getEpic() {
        super.getEpic();
    }

    @Test
    @Override
    void getWrongEpic() {
        super.getWrongEpic();
    }

    @Test
    @Override
    void getEpicForVoidEpicList() {
        super.getEpicForVoidEpicList();
    }

    @Test
    @Override
    void getSubTask() {
        super.getSubTask();
    }

    @Test
    @Override
    void getWrongSubtask() {
        super.getWrongSubtask();
    }

    @Test
    @Override
    void getSubTskForVoidSubTaskList() {
        super.getSubTskForVoidSubTaskList();
    }

    @Test
    @Override
    void createTask() {
        super.createTask();
    }

    @Test
    @Override
    void createEpic() {
        super.createEpic();
    }

    @Test
    @Override
    void createSubTask() {
        super.createSubTask();
    }

    @Test
    @Override
    void createSubTaskWithWrongEpic() {
        super.createSubTaskWithWrongEpic();
    }

    @Test
    @Override
    void getAllTasks() {
        super.getAllTasks();
    }

    @Test
    @Override
    void getAllTasksForVoidTaskList() {
        super.getAllTasksForVoidTaskList();
    }

    @Test
    @Override
    void getAllEpics() {
        super.getAllEpics();
    }

    @Test
    @Override
    void getAllTasksForVoidEpicList() {
        super.getAllTasksForVoidEpicList();
    }

    @Test
    @Override
    void getAllSubTasks() {
        super.getAllSubTasks();
    }

    @Test
    @Override
    void getAllTasksForVoidSubTaskList() {
        super.getAllTasksForVoidSubTaskList();
    }

    @Test
    @Override
    void getAllSubTasksForEpic() {
        super.getAllSubTasksForEpic();
    }

    @Test
    @Override
    void getAllSubTasksForEpicWithVoidSubTaskList() {
        super.getAllSubTasksForEpicWithVoidSubTaskList();
    }

    @Test
    @Override
    void removeTask() {
        super.removeTask();
    }

    @Test
    @Override
    void removeEpic() {
        super.removeEpic();
    }

    @Test
    @Override
    void removeSubTask() {
        super.removeSubTask();
    }

    @Test
    @Override
    void removeAllTasks() {
        super.removeAllTasks();
    }

    @Test
    @Override
    void updateTask() {
        super.updateTask();
    }

    @Test
    @Override
    void updateEpicWithSubTasks() {
        super.updateEpicWithSubTasks();
    }

    @Test
    @Override
    void updateEpicWithoutSubTasks() {
        super.updateEpicWithoutSubTasks();
    }

    @Test
    @Override
    void updateSubTask() {
        super.updateSubTask();
    }
}
