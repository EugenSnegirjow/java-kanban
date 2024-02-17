package task.manager.service.autoSaver;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.model.TypeOfTasks;
import task.manager.service.Managers;
import task.manager.service.historyManager.HistoryManager;
import task.manager.service.taskManager.InMemoryTaskManager;
import task.manager.service.taskManager.Status;
import task.manager.service.taskManager.TaskManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private Path file;

    public FileBackedTasksManager(Path file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(Path file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try (BufferedReader bufferedReader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            bufferedReader.readLine();
            while (bufferedReader.ready() || !(bufferedReader.readLine().equals(""))) {
                Task task = fromString(bufferedReader.readLine());
                if (task instanceof SubTask) {
                    fileBackedTasksManager.create(((SubTask) task).getEpicTaskId(), (SubTask) task);
                } else if (task instanceof Epic) {
                    fileBackedTasksManager.create((Epic) task);
                } else {
                    fileBackedTasksManager.create(task);
                }
            }
            bufferedReader.readLine();
            List<Integer> history = historyFromString(bufferedReader.readLine());
            HistoryManager historyManager = Managers.getDefaultHistory();
            for (Integer taskId : history) {
                historyManager.add(fileBackedTasksManager.getTask(taskId));
            }
        } catch (IOException e) {
            System.out.println("Не найден файл сохранения");
        }
        return fileBackedTasksManager;
    }

    static List<Integer> historyFromString(String value) {
        String[] values = value.split(",");
        List<Integer> tasks = new LinkedList<>();
        for (String s : values) {
            tasks.add(Integer.parseInt(s));
        }
        return tasks;
    }

    private static Task fromString(String value) {
        String[] taskDescription = value.split(","); //id,type,name,status,description,epic
        TypeOfTasks type = TypeOfTasks.valueOf(taskDescription[1]);
        switch (type) {
            case TASK:
                return new Task(Integer.parseInt(taskDescription[0]),
                        taskDescription[2],
                        Status.valueOf(taskDescription[3]),
                        taskDescription[4]);
            case EPIC:
                return new Epic(Integer.parseInt(taskDescription[0]),
                        taskDescription[2],
                        Status.valueOf(taskDescription[3]),
                        taskDescription[4]);
            case SUBTASK:
                return new SubTask(Integer.parseInt(taskDescription[0]),
                        taskDescription[2],
                        Status.valueOf(taskDescription[3]),
                        taskDescription[4],
                        Integer.parseInt(taskDescription[5]));
            default:
                System.out.println("Неверный тип задачи");
        }
        return null;
    }

    static String historyToString(HistoryManager manager) throws IOException {
        ArrayList<Integer> history = new ArrayList<>();
        if (manager.getHistory() == null || manager.getHistory().size() == 0) {
            throw new IOException("Истории нет");
        }
        for (Task task : manager.getHistory()) {
            history.add(task.getId());
        }
        String[] historyArray = new String[history.size()];
        for (int i = 0; i < history.size(); i++) {
            historyArray[i] = history.get(i).toString();
        }
        return String.join(",", historyArray);
    }

    private void save() {
        try {
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            System.out.println("Не удалось создать файл");
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.toFile(), StandardCharsets.UTF_8))) {

            bufferedWriter.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                bufferedWriter.write(task.toString());
            }
            for (Task epic : getAllEpics()) {
                bufferedWriter.write(epic.toString());
            }
            for (Task subTask : getAllSubTasks()) {
                bufferedWriter.write(subTask.toString());
            }

            bufferedWriter.write("\n");
            try {
                bufferedWriter.write(historyToString(getHistoryManager()));
            } catch (IOException e) {
                System.out.println("Нет истории");
                bufferedWriter.write("");
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время записи файла");
        }
        System.out.println("Сохранение прошло успешно");
    }

    @Override
    public Task getTask(int taskId) {
        Task task = super.getTask(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int taskId) {
        Epic epic = super.getEpic(taskId);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int taskId) {
        SubTask subTask = super.getSubTask(taskId);
        save();
        return subTask;
    }

    @Override
    public int create(Task task) {
        int id = super.create(task);
        save();
        return id;
    }

    @Override
    public int create(Epic task) {
        int id = super.create(task);
        save();
        return id;
    }

    @Override
    public int create(int epic, SubTask subTask) {
        int id = super.create(epic, subTask);
        save();
        return id;
    }

    @Override
    public void removeTask(Integer taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeEpic(Integer taskId) {
        super.removeEpic(taskId);
        save();
    }

    @Override
    public void removeSubTask(Integer taskId) {
        super.removeSubTask(taskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public void update(Epic task) {
        super.update(task);
        save();
    }

    @Override
    public void update(SubTask task) {
        super.update(task);
        save();
    }
}
