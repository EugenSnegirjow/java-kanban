import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.HistoryManager;
import task.manager.service.Managers;
import task.manager.service.TaskManager;

import java.util.ArrayList;


public class Main {
    static Task task;
    static Epic epic;
    static SubTask subTask;
    static ArrayList<Integer> allTasksIDs = new ArrayList<>();

    public static void main(String[] args) {
        Managers managers = new Managers();
        TaskManager manager = managers.getDefault();
        for (int i = 1; i <= 5; i++) {
            task = new Task("Простая задача " + i, "Описание простой задачи " + i);
            allTasksIDs.add(manager.create(task));
        }

        for (int i = 1; i <= 2; i++) {
            epic = new Epic("Сложная задача " + i, "Описание сложной задачи " + i);
            allTasksIDs.add(manager.create(epic));
            for (int j = 1; j <= 2; j++) {
                subTask = new SubTask("Подзадача " + j + " сложной задачи " + i,
                        "Описание подзадачи " + j + " сложной задачи " + i, epic.getId());
                allTasksIDs.add(manager.create(subTask));
            }
        }

        HistoryManager historyManager = Managers.getDefaultHistory();

        ArrayList<Task> allTasks = manager.getAllTasks();
        for (Task task : allTasks) {
            historyManager.getTask(task.getId());
        }

        ArrayList<Task> allEpics = manager.getAllEpics();
        for (Task task : allEpics) {
            historyManager.getEpic(task.getId());
        }

        ArrayList<Task> allSubTasks = manager.getAllSubTasks();
        for (Task task : allSubTasks) {
            historyManager.getSubTask(task.getId());
        }

        ArrayList<Task> history = historyManager.getHistory();
        for (Task task1 : history) {
            System.out.println(task1.getTitle() + "\n" + task1.getDescription());
        }

    }

    public static void printMenu() {
        System.out.println("1 - ввести простую задачу \n" +
                "2 - ввести сложную задачу\n" +
                "3 - ввести подзадачу\n" +
                "4 - \n" +
                "5 - \n" +
                "6 - \n" +
                "7 - \n" +
                "8 - \n" +
                "9 - \n" +
                "10 - ");
    }
}
