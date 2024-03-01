package task.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.historyManager.HistoryManager;
import task.manager.service.taskManager.TaskManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest {

    HistoryManager historyManager;
    TaskManager taskManager;

    @BeforeEach
    public void createManagers() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2");
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");
        SubTask subTask1 = new SubTask("subTask2.1", "Description subTask1");
        SubTask subTask2 = new SubTask("subTask2.2", "Description subTask2");
        taskManager = Managers.getDefault();
        taskManager.create(task1);
        taskManager.create(task2);
        taskManager.create(epic1);
        taskManager.create(epic2);
        taskManager.create(epic2.getId(), subTask1);
        taskManager.create(epic2.getId(), subTask2);
        historyManager = taskManager.getHistoryManager();
    }

    @Test
    void add() {
        ArrayList<Task> voidList = new ArrayList<>();
        assertEquals(voidList, historyManager.getHistory());

        ArrayList<Task> expectedList = new ArrayList<>();
        expectedList.add(taskManager.getTask(1));
        expectedList.add(taskManager.getEpic(3));
        expectedList.add(taskManager.getSubTask(5));
        historyManager.add(taskManager.getTask(1));
        historyManager.add(taskManager.getEpic(3));
        historyManager.add(taskManager.getSubTask(5));

        assertEquals(expectedList, historyManager.getHistory());
    }

    @Test
    void addRepetitiveTask() {
        ArrayList<Task> voidList = new ArrayList<>();
        assertEquals(voidList, historyManager.getHistory());

        ArrayList<Task> expectedList = new ArrayList<>();
        expectedList.add(taskManager.getEpic(3));
        expectedList.add(taskManager.getSubTask(5));
        expectedList.add(taskManager.getTask(1));

        historyManager.add(taskManager.getSubTask(5));
        historyManager.add(taskManager.getTask(1));
        historyManager.add(taskManager.getEpic(3));
        historyManager.add(taskManager.getEpic(3));
        historyManager.add(taskManager.getSubTask(5));
        historyManager.add(taskManager.getTask(1));

        assertEquals(expectedList, historyManager.getHistory());
    }

    @Test
    void removeFromStartHistory() {
        ArrayList<Task> tasksList = new ArrayList<>();

        tasksList.add(taskManager.getTask(1));
        tasksList.add(taskManager.getEpic(3));
        tasksList.add(taskManager.getSubTask(5));

        historyManager.add(taskManager.getTask(1));
        historyManager.add(taskManager.getEpic(3));
        historyManager.add(taskManager.getSubTask(5));

        assertEquals(tasksList, historyManager.getHistory());

        ArrayList<Task> expectedList = new ArrayList<>();
        expectedList.add(taskManager.getEpic(3));
        expectedList.add(taskManager.getSubTask(5));
        historyManager.remove(1);

        assertEquals(expectedList, historyManager.getHistory());
    }

    @Test
    void removeFromEndHistory() {
        ArrayList<Task> tasksList = new ArrayList<>();

        tasksList.add(taskManager.getTask(1));
        tasksList.add(taskManager.getEpic(3));
        tasksList.add(taskManager.getSubTask(5));

        historyManager.add(taskManager.getTask(1));
        historyManager.add(taskManager.getEpic(3));
        historyManager.add(taskManager.getSubTask(5));

        assertEquals(tasksList, historyManager.getHistory());

        ArrayList<Task> expectedList = new ArrayList<>();
        expectedList.add(taskManager.getTask(1));
        expectedList.add(taskManager.getEpic(3));
        historyManager.remove(5);

        assertEquals(expectedList, historyManager.getHistory());
    }

    @Test
    void removeWrongTask() {
        historyManager.add(taskManager.getTask(1));
        historyManager.add(taskManager.getEpic(3));
        historyManager.add(taskManager.getSubTask(5));

        ArrayList<Task> expectedList = new ArrayList<>();
        expectedList.add(taskManager.getTask(1));
        expectedList.add(taskManager.getEpic(3));
        expectedList.add(taskManager.getSubTask(5));
        historyManager.remove(2);

        assertEquals(expectedList, historyManager.getHistory());
    }

    /** Нужна ли какая-то дополнительна проверка метода getHistory,
     если я протестировал его в тестах методов add и removeHistory? */
    @Test
    void getHistory() {

    }

    @Test
    void removeHistory() {
        ArrayList<Task> tasksList = new ArrayList<>();

        tasksList.add(taskManager.getTask(1));
        tasksList.add(taskManager.getEpic(3));
        tasksList.add(taskManager.getSubTask(5));

        historyManager.add(taskManager.getTask(1));
        historyManager.add(taskManager.getEpic(3));
        historyManager.add(taskManager.getSubTask(5));

        assertEquals(tasksList, historyManager.getHistory());

        ArrayList<Task> expectedVoidList = new ArrayList<>();
        historyManager.removeHistory();
        assertEquals(expectedVoidList, historyManager.getHistory());
    }
    @Test
    void removeHistoryFromVoidHistory() {
        ArrayList<Task> expectedVoidList = new ArrayList<>();
        assertEquals(expectedVoidList, historyManager.getHistory());

        historyManager.removeHistory();
        assertEquals(expectedVoidList, historyManager.getHistory());
    }
}