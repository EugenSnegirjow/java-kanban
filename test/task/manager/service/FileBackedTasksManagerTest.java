package task.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.manager.enums.Status;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.autoSaver.FileBackedTasksManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    Path save = Paths.get("src\\task\\manager\\resources\\Save.csv");

    @BeforeEach
    @Override
    public void createManagers() {
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

        manager = new FileBackedTasksManager(save);
        manager.create(task1);
        manager.create(task2);
        manager.create(epic1);
        manager.create(epic2);
        manager.create(epic2.getId(), subTask1);
        manager.create(epic2.getId(), subTask2);
        manager.getTask(1);
        manager.getSubTask(5);
        manager.getEpic(4);
        manager.getEpic(3);
    }

    @Test
    public void saveAndLoad() {
        FileBackedTasksManager managerLoadFromFile = FileBackedTasksManager.loadFromFile(save);
        assertEquals(manager.toString(), managerLoadFromFile.toString());
    }
}