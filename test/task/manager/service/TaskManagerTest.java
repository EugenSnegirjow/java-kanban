package task.manager.service;

import task.manager.enums.Status;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.exception.NotFoundException;
import task.manager.service.taskManager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static task.manager.enums.Status.DONE;
import static task.manager.enums.Status.NEW;

abstract class TaskManagerTest<T extends TaskManager> {

    static TaskManager manager;
    static TaskManager voidManager;

    public void createManagers() {
        voidManager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", Status.NEW, "Description Task1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), Duration.ofMinutes(15));
        Task task2 = new Task(1, "Task2", Status.NEW, "Description Task2",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(30), Duration.ofMinutes(15));
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");
        SubTask subTask1 = new SubTask(1, "SubTask1", Status.NEW, "Description SubTask1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(60), Duration.ofMinutes(15), 4);
        SubTask subTask2 = new SubTask(1, "SubTask2", Status.NEW, "Description SubTask2",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(90), Duration.ofMinutes(15), 4);
        manager = Managers.getDefault();
        manager.create(task1);
        manager.create(task2);
        manager.create(epic1);
        manager.create(epic2);
        manager.create(epic2.getId(), subTask1);
        manager.create(epic2.getId(), subTask2);
    }

    public void getManagerHistory() {
        ArrayList<Task> expected = new ArrayList<>();
        expected.add(manager.getTask(1));
        expected.add(manager.getTask(2));
        expected.add(manager.getSubTask(5));
        expected.add(manager.getEpic(4));
        List<Task> tasksHistory = manager.getManagerHistory();
        assertEquals(expected, tasksHistory);
    }

    public void getManagerHistoryForVoidHistory() {
        List<Task> tasksHistory = voidManager.getManagerHistory();
        List<Task> expected = new ArrayList<>();
        assertEquals(expected, tasksHistory);
    }

    void getTask() {
        Task actual = manager.getTask(1);
        Task expected = new Task(1, "Task1", Status.NEW, "Description Task1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), Duration.ofMinutes(15));
        assertEquals(expected, actual);
    }

    void getWrongTask() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> manager.getTask(3));
        assertEquals("Задачи с ID 3 не существует", exception.getMessage());
    }

    void getTaskForVoidTaskList() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> manager.getTask(3));
        assertEquals("Задачи с ID 3 не существует", exception.getMessage());
    }

    void getEpic() {
        Task actual = manager.getEpic(3);
        Task expected = new Epic(3, "Epic1", NEW, "Description Epic1",
                manager.getEpic(3).getStartTime(), manager.getEpic(3).getDuration());
        assertEquals(expected, actual);
    }

    void getWrongEpic() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> manager.getEpic(1));
        assertEquals("Эпика с ID 1 не существует", exception.getMessage());
    }

    void getEpicForVoidEpicList() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> manager.getEpic(1));
        assertEquals("Эпика с ID 1 не существует", exception.getMessage());
    }

    void getSubTask() {
        Task actual = manager.getSubTask(5);
        Task expected = new SubTask(5, "SubTask1", Status.NEW, "Description SubTask1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(60), Duration.ofMinutes(15), 4);
        assertEquals(expected, actual);
    }

    void getWrongSubtask() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> manager.getSubTask(1));
        assertEquals("Подзадачи с ID 1 не существует", exception.getMessage());
    }

    void getSubTskForVoidSubTaskList() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> manager.getSubTask(1));
        assertEquals("Подзадачи с ID 1 не существует", exception.getMessage());
    }

    void createTask() {
        Task task1 = new Task(1, "Task1", Status.NEW, "Description Task1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), Duration.ofMinutes(15));
        Task task2 = new Task(2, "Task2", Status.NEW, "Description Task2",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(30), Duration.ofMinutes(15));
        ArrayList<Task> expected = new ArrayList<>();
        expected.add(task1);
        expected.add(task2);
        Integer id = voidManager.create(task1);
        voidManager.create(task2);
        assertEquals(expected, voidManager.getAllTasks());
        assertEquals(1, id);
    }

    void createEpic() {
        Epic task1 = new Epic(1, "Task1", NEW, "Description Task1");
        Epic task2 = new Epic(2, "Task2", NEW, "Description Task2");
        ArrayList<Epic> expected = new ArrayList<>();
        expected.add(task1);
        expected.add(task2);
        Integer id = voidManager.create(task1);
        voidManager.create(task2);
        assertEquals(expected, voidManager.getAllEpics());
        assertEquals(1, id);
    }

    void createSubTask() {
        SubTask task1 = new SubTask(5, "SubTask1", Status.NEW, "Description SubTask1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(60), Duration.ofMinutes(15), 4);
        SubTask task2 = new SubTask(6, "SubTask2", Status.NEW, "Description SubTask2",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(90), Duration.ofMinutes(15), 4);
        ArrayList<SubTask> expected = new ArrayList<>();
        expected.add(task1);
        expected.add(task2);
        Epic epic = new Epic("Epic2", "Description Epic2");
        voidManager.create(epic);
        Integer id = voidManager.create(task1);
        voidManager.create(task2);
        assertEquals(expected, voidManager.getAllTasks());
        assertEquals(2, id);
    }

    void createSubTaskWithWrongEpic() {
        SubTask task1 = new SubTask(2, "Task1", NEW, "Description Task1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusHours(3), Duration.ofMinutes(15), 1);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> voidManager.create(2, task1)
        );
        assertEquals("Эпика с ID " + 2 + " не существует. Подзадача не добавлена", exception.getMessage());
    }

    void getAllTasks() {
        ArrayList<Task> expected = new ArrayList<>();
        expected.add(manager.getTask(1));
        expected.add(manager.getTask(2));
        ArrayList<Task> actual = manager.getAllTasks();
        assertEquals(expected, actual);
    }

    void getAllTasksForVoidTaskList() {
        ArrayList<Task> expected = new ArrayList<>();
        ArrayList<Task> actual = voidManager.getAllTasks();
        assertEquals(expected, actual);
    }

    void getAllEpics() {
        ArrayList<Task> expected = new ArrayList<>();
        expected.add(manager.getEpic(3));
        expected.add(manager.getEpic(4));
        ArrayList<Task> actual = manager.getAllEpics();
        assertEquals(expected, actual);
    }

    void getAllTasksForVoidEpicList() {
        ArrayList<Task> expected = new ArrayList<>();
        ArrayList<Task> actual = voidManager.getAllEpics();
        assertEquals(expected, actual);
    }

    void getAllSubTasks() {
        ArrayList<Task> expected = new ArrayList<>();
        expected.add(manager.getSubTask(5));
        expected.add(manager.getSubTask(6));
        ArrayList<Task> actual = manager.getAllSubTasks();
        assertEquals(expected, actual);
    }

    void getAllTasksForVoidSubTaskList() {
        ArrayList<Task> expected = new ArrayList<>();
        ArrayList<Task> actual = voidManager.getAllSubTasks();
        assertEquals(expected, actual);
    }

    void getAllSubTasksForEpic() {
        SubTask subTask1 = new SubTask(5, "SubTask1", Status.NEW, "Description SubTask1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(60), Duration.ofMinutes(15), 4);
        SubTask subTask2 = new SubTask(6, "SubTask2", Status.NEW, "Description SubTask2",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(90), Duration.ofMinutes(15), 4);
        ArrayList<Task> expected = new ArrayList<>();

        expected.add(subTask1);
        expected.add(subTask2);

        ArrayList<Task> actual = manager.getAllSubTasksForEpic(manager.getEpic(4));
        assertEquals(expected, actual);
    }

    void getAllSubTasksForEpicWithVoidSubTaskList() {

        ArrayList<Task> expected = new ArrayList<>();
        ArrayList<Task> actual = manager.getAllSubTasksForEpic(manager.getEpic(3));
        assertEquals(expected, actual);
    }

    void removeTask() {
        ArrayList<Task> expected = new ArrayList<>();
        expected.add(manager.getTask(2));
        manager.removeTask(1);
        ArrayList<Task> actual = manager.getAllTasks();
        assertEquals(expected, actual);
    }

    void removeEpic() {
        ArrayList<Task> expected = new ArrayList<>();
        expected.add(manager.getEpic(4));
        manager.removeEpic(3);
        ArrayList<Task> actual = manager.getAllEpics();
        assertEquals(expected, actual);
    }

    void removeSubTask() {
        ArrayList<Task> expected = new ArrayList<>();
        expected.add(manager.getSubTask(6));
        manager.removeSubTask(5);
        ArrayList<Task> actual = manager.getAllSubTasks();
        assertEquals(expected, actual);
    }

    void removeAllTasks() {
        manager.removeAllTasks();
        ArrayList<Task> expected = new ArrayList<>();

        ArrayList<Task> actual1 = manager.getAllTasks();
        assertEquals(expected, actual1);

        ArrayList<Task> actual2 = manager.getAllEpics();
        assertEquals(expected, actual2);

        ArrayList<Task> actual3 = manager.getAllSubTasks();
        assertEquals(expected, actual3);
    }

    void updateTask() {
        Task task1 = new Task(1, "Task1", Status.NEW, "Description Task1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), Duration.ofMinutes(15));
        assertEquals(task1, manager.getTask(1));

        Task taskNew = new Task(1, "Task2", DONE, "Description Task2",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), Duration.ofMinutes(15));
        manager.update(taskNew);
        assertEquals(taskNew, manager.getTask(1));
    }

    void updateEpicWithSubTasks() {
        Epic task1 = new Epic(4, "Epic2", NEW, "Description Epic2",
                manager.getSubTask(5).getStartTime(),
                Duration.between(manager.getSubTask(5).getStartTime(), manager.getSubTask(6).getEndTime()));
        task1.addSubTaskId(5);
        task1.addSubTaskId(6);
        assertEquals(task1, manager.getEpic(4));
        assertEquals(NEW, manager.getSubTask(5).getStatus());

        Epic taskNew = new Epic(4, "Epic2", DONE, "Description Epic2");
        manager.update(taskNew);
        assertEquals(taskNew, manager.getEpic(4));
    }

    void updateEpicWithoutSubTasks() {
        Epic task1 = new Epic(3, "Epic1", NEW, "Description Epic1");
        assertEquals(task1, manager.getEpic(3));

        Epic taskNew = new Epic(3, "Epic1", DONE, "Description Epic1");
        manager.update(taskNew);
        assertEquals(taskNew, manager.getEpic(3));
    }

    void updateSubTask() {
        SubTask subTask1 = new SubTask(5, "SubTask1", Status.NEW, "Description SubTask1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(60), Duration.ofMinutes(15), 4);
        assertEquals(subTask1, manager.getSubTask(5));
        Status oldEpicStatus = manager.getEpic(4).getStatus();
        assertEquals(NEW, oldEpicStatus);

        subTask1 = new SubTask(5, "SubTask1", DONE, "Description SubTask1",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(60), Duration.ofMinutes(15), 4);
        SubTask subTask2 = new SubTask(6, "SubTask2", DONE, "Description SubTask2",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(90), Duration.ofMinutes(15), 4);
        manager.update(subTask1);
        manager.update(subTask2);
        assertEquals(subTask1, manager.getSubTask(5));
        Status newEpicStatus = manager.getEpic(4).getStatus();
        assertEquals(DONE, newEpicStatus);
    }

}

