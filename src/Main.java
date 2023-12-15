import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.TaskManager;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static Task task;
    static Epic epic;
    static SubTask subTask;
    static TaskManager taskManager = new TaskManager();
    static Scanner scanner = new Scanner(System.in);
    static int taskID;
    static HashMap<Integer, Task> allTasks = new HashMap<>();

    public static void main(String[] args) {

        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:  // ввести простую задачу
                    task.setTitle("Простая задача");
                    task.setDescription("Описание задачи");
                    taskID = taskManager.create(task);
                    allTasks.put(taskID, task);
                    break;
                case 2:  // ввести сложную задачу
                    epic.setTitle("Сложная задача");
                    epic.setDescription("Описание задачи");
                    taskManager.create(epic);
                    taskID = taskManager.create(epic);
                    allTasks.put(taskID, epic);
                    break;
                case 3:  // ввести подзадачу
                    int epicID = 22;
                    subTask.setTitle("Подзадача задача");
                    subTask.setDescription("Описание задачи");
                    taskManager.create(epicID, subTask);
                    break;
                case 4:  // изменить простую задачу
                    taskManager.update(task);
                    break;
                case 5:  // изменить сложную задачу
                    taskManager.update(epic);
                    break;
                case 6:  // изменить подзадачу
                    subTask.setStatus("DONE");
                    taskManager.update(subTask);
                    break;
                case 7:  // удалить простую задачу
                    taskManager.removeTask(task.getID());
                    break;
                case 8:  // удалить сложную задачу
                    taskManager.removeEpic(epic.getID());
                    break;
                case 9:  // удалить подзадачу
                    taskManager.removeSubTask(subTask.getID());
                    break;
                case 10:  // удалить все задачи
                    taskManager.removeAllTasks();
                case 11:  // получить список всех задач
                    taskManager.printAllTasks();
                case 12:  // получить список подзадач эпика
                    taskManager.printEpicWithSubTasks(subTask.getID());
                    return;
                default: //завершить работу
                    break;

            }
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
