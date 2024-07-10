package task.manager.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.manager.service.Managers;
import task.manager.service.taskManager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static task.manager.enums.Status.*;

public class EpicStatusTest {

    static SubTask subTaskDONE;
    static SubTask subTaskNEW;
    static SubTask subTaskIN_PROGRESS;
    Epic epic;
    TaskManager manager;


    @BeforeAll
    public static void createSubtasks() {
        subTaskNEW = new SubTask(2, "Новая подзадача", NEW, "Описание подзадачи",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(60), Duration.ofMinutes(15), 1);
        subTaskDONE = new SubTask(3, "Выполненная подзадача", DONE, "Описание подзадачи",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(90), Duration.ofMinutes(15), 1);
        subTaskIN_PROGRESS = new SubTask(4, "Подзадача в процессе", IN_PROGRESS, "Описание подзадачи",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(120), Duration.ofMinutes(15), 1);
    }


    @BeforeEach
    public void createManagerWithEpic() {
        manager = Managers.getDefault();
        epic = new Epic("Эпическая задача", "Описание эпической задачи");
        manager.create(epic);
    }

    @Test
    public void getStatusForVoidSubtaskLst() {
        assertEquals(NEW, epic.getStatus());
    }

    @Test
    public void getStatusForAllNEWSubtaskLst() {
        manager.create(1, subTaskNEW);
        assertEquals(NEW, epic.getStatus());
    }

    @Test
    public void getStatusForAllDONESubtaskLst() {
        manager.create(1, subTaskDONE);
        assertEquals(DONE, epic.getStatus());
    }

    @Test
    public void getStatusForNEWAndDONESubtaskLst() {
        manager.create(1, subTaskNEW);
        manager.create(1, subTaskDONE);
        assertEquals(IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void getStatusForIN_PROGRESSSubtaskLst() {
        manager.create(1, subTaskIN_PROGRESS);
        assertEquals(IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void getStatusForRemoveSubtaskLst() {
        manager.create(1, subTaskNEW);
        manager.create(1, subTaskDONE);
        manager.create(1, subTaskIN_PROGRESS);
        manager.removeSubTask(2);
        manager.removeSubTask(3);
        manager.removeSubTask(4);
        assertEquals(NEW, epic.getStatus());
    }
}
