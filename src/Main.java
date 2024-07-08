import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.Managers;
import task.manager.service.autoSaver.FileBackedTasksManager;
import task.manager.service.taskManager.TaskManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static task.manager.enums.Status.NEW;


public class Main {
    static Task task;
    static Epic epic;
    static SubTask subTask;
    static ArrayList<Integer> allTasksIDs = new ArrayList<>();

    public void test() {
        Path save = Paths.get("src\\task\\manager\\resources\\Save.csv");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        FileBackedTasksManager saver = new FileBackedTasksManager(save);

        TaskManager manager = Managers.getDefault();
        for (int i = 1; i <= 5; i++) {
            String date = "11.01.2" + i + " 10:00";
            task = new Task(
                    i,
                    "Простая задача " + i,
                    NEW,
                    "Описание простой задачи ",
                    LocalDateTime.parse(date, formatter),
                    Duration.ofMinutes(50)
            );
            allTasksIDs.add(manager.create(task));
            saver.create(task);
        }

        for (int i = 1; i <= 2; i++) {
            epic = new Epic("Сложная задача " + i, "Описание сложной задачи " + i);
            allTasksIDs.add(manager.create(epic));
            saver.create(epic);
        }

        int day = 0;
        for (int i = 3; i <= 4; i++, day++) {
            epic = new Epic("Сложная задача " + i, "Описание сложной задачи " + i);
            allTasksIDs.add(manager.create(epic));
            saver.create(epic);
            for (int j = 1; j <= 3; j++, day++) {
                String date = "11.02.0" + day + " 1" + i +":0" + j;
                subTask = new SubTask(j,
                        "Подзадача " + j + " сложной задачи " + i,
                        NEW,
                        "Описание подзадачи " + j + " сложной задачи " + i,
                        LocalDateTime.parse(date, formatter),
                        Duration.ofMinutes(50),
                        i);//, epic.getId()
                allTasksIDs.add(manager.create(epic.getId(), subTask));
                saver.create(epic.getId(), subTask);
            }
        }

        manager.getTask(1);
        manager.getTask(3);
        manager.getTask(1);
        manager.getTask(4);
        manager.getTask(3);
        manager.getEpic(6);
        manager.getEpic(7);
        manager.getEpic(8);
        manager.getSubTask(9);
        manager.getSubTask(10);
        manager.getSubTask(11);
        manager.getEpic(6);
        manager.getSubTask(13);
        manager.getSubTask(14);
        manager.getSubTask(15);

        saver.getTask(1);
        saver.getTask(3);
        saver.getTask(1);
        saver.getTask(4);
        saver.getTask(3);
        saver.getEpic(6);
        saver.getEpic(7);
        saver.getEpic(8);
        saver.getSubTask(9);
        saver.getSubTask(10);
        saver.getSubTask(11);
        saver.getEpic(6);
        saver.getSubTask(13);
        saver.getSubTask(14);
        saver.getSubTask(15);

        List<Task> history = manager.getManagerHistory();
        for (Task task1 : history) {
            System.out.println(task1.getTitle() + ", id - " + task1.getId());
        }
        System.out.println("--------------------");

        manager.removeTask(3);
        manager.removeTask(1);

        System.out.println("Перед удалением эпика 8");
        manager.removeEpic(8);
        history = manager.getManagerHistory();
        for (Task task1 : history) {
            System.out.println(task1.getTitle() + ", id - " + task1.getId());
        }

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(save);
        for (Task subTask : fileBackedTasksManager.getAllSubTasks()) {
            System.out.println(subTask);
        }
    }

}
