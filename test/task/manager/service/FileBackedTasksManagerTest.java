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
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{

    Path save = Paths.get("src\\task\\manager\\resources\\Save.csv");
    @BeforeEach
    @Override
    public void createManagers() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2");
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");
        SubTask subTask1 = new SubTask(5,
                "subTask2.1",
                Status.NEW,
                "Description subTask1",
                LocalDateTime.parse("04.03.24 23:35", formatter),
                Duration.ofMinutes(5), 4
                );
        SubTask subTask2 = new SubTask(6,
                "subTask2.2",
                Status.NEW,
                "Description subTask2",
                LocalDateTime.parse("04.03.24 23:45", formatter),
                Duration.ofMinutes(5), 4);
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
//        manager.removeEpic(4);
    }

    @Test
    public void saveAndLoad () {
        FileBackedTasksManager managerLoadFromFile = FileBackedTasksManager.loadFromFile(save);
        assertEquals(manager, managerLoadFromFile);
    }
}