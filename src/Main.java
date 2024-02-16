import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.Managers;
import task.manager.service.autoSaver.FileBackedTasksManager;
import task.manager.service.historyManager.HistoryManager;
import task.manager.service.taskManager.TaskManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Main {
    static Task task;
    static Epic epic;
    static SubTask subTask;
    static ArrayList<Integer> allTasksIDs = new ArrayList<>();


    static FileBackedTasksManager loadFromFile(Path file) {


        return null;
    }

    public static void main(String[] args) throws IOException {
        Path save = Paths.get("src\\task\\manager\\resources\\Save.csv");

        FileBackedTasksManager saver = new FileBackedTasksManager(save);


        TaskManager manager = Managers.getDefault();
        for (int i = 1; i <= 5; i++) {
            task = new Task("Простая задача " + i, "Описание простой задачи " + i);
            allTasksIDs.add(manager.create(task));
            saver.create(task);
        }

        for (int i = 1; i <= 2; i++) {
            epic = new Epic("Сложная задача " + i, "Описание сложной задачи " + i);
            allTasksIDs.add(manager.create(epic));
            saver.create(epic);
        }

        for (int i = 3; i <= 4; i++) {
            epic = new Epic("Сложная задача " + i, "Описание сложной задачи " + i);
            allTasksIDs.add(manager.create(epic));
            saver.create(epic);
            for (int j = 1; j <= 3; j++) {
                subTask = new SubTask("Подзадача " + j + " сложной задачи " + i,
                        "Описание подзадачи " + j + " сложной задачи " + i);//, epic.getId()
                allTasksIDs.add(manager.create(epic.getId(), subTask));
                saver.create(epic.getId(), subTask);
            }
        }

        HistoryManager historyManager = Managers.getDefaultHistory();

        ArrayList<Task> allTasks = manager.getAllTasks();
//        for (Task task : allTasks) {
//            manager.getTask(task.getId());
//        }
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

        ArrayList<Task> history = manager.getManagerHistory();
        for (Task task1 : history) {
            //System.out.println(task1.getTitle() + task1.getId());// + "\n" + task1.getDescription());
            System.out.println(task1.getTitle() + ", id - " + task1.getId());// + "\n" + task1.getDescription());
        }
        System.out.println("--------------------");

        manager.removeTask(3);
        manager.removeTask(1);

        manager.removeEpic(8);
        history = manager.getManagerHistory();
        for (Task task1 : history) {
            //System.out.println(task1.getTitle() + task1.getId());// + "\n" + task1.getDescription());
            System.out.println(task1.getTitle() + ", id - " + task1.getId());// + "\n" + task1.getDescription());
        }

    }

}
