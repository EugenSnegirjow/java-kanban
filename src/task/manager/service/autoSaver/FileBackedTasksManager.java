package task.manager.service.autoSaver;

import task.manager.enums.Status;
import task.manager.enums.TypeOfTasks;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.exception.ManagerSaveException;
import task.manager.service.historyManager.HistoryManager;
import task.manager.service.taskManager.InMemoryTaskManager;
import task.manager.service.taskManager.TaskManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private Path file;
    public static final String SAVE_TITLE = "id,type,name,status,description,startTime,endTime,duration,epic";

    public FileBackedTasksManager(Path file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(Path file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try (BufferedReader bufferedReader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            bufferedReader.readLine();
            TreeSet<Task> tasksSortedById = new TreeSet<>(Comparator.comparingInt(Task::getId));
            boolean isEmpty = false;
            while (!isEmpty) {
                Task task = fromString(bufferedReader.readLine());
                if (task != null) {
                    tasksSortedById.add(task);
                } else {
                    isEmpty = true;
                }
            }

            List<Integer> history = historyFromString(bufferedReader.readLine());

            for (Task task : tasksSortedById) {


                /** Не понимаю как заменить на switch-case. Так это не работает, и IDE предлагает заменить на else-if
//                switch (task.getClass()) {
//                    case SubTask.class:
//                        fileBackedTasksManager.subTasks.put(task.getId(), (SubTask) task);
//                        fileBackedTasksManager.epics.get(((SubTask) task).getEpicTaskId()).addSubTaskId(task.getId());
//                        fileBackedTasksManager.sortedSubTasks.add((SubTask) task);
//                        fileBackedTasksManager.sortedTasksAndSubTasks.add(task);
//                        break;
//                    case Epic.class:
//                        fileBackedTasksManager.epics.put(task.getId(), (Epic) task);
//                        break;
//                    default:
//                        fileBackedTasksManager.tasks.put(task.getId(), task);
//                        fileBackedTasksManager.sortedTasks.add(task);
//                        fileBackedTasksManager.sortedTasksAndSubTasks.add(task);
//                }*/

                if (task.getClass().equals(SubTask.class)) {
                    fileBackedTasksManager.subTasks.put(task.getId(), (SubTask) task);
                    fileBackedTasksManager.epics.get(((SubTask) task).getEpicTaskId()).addSubTaskId(task.getId());
                    fileBackedTasksManager.sortedSubTasks.add((SubTask) task);
                    fileBackedTasksManager.sortedTasksAndSubTasks.add(task);
                } else if (task.getClass().equals(Epic.class)) {
                    fileBackedTasksManager.epics.put(task.getId(), (Epic) task);
                } else {
                    fileBackedTasksManager.tasks.put(task.getId(), task);
                    fileBackedTasksManager.sortedTasks.add(task);
                    fileBackedTasksManager.sortedTasksAndSubTasks.add(task);
                }
            }
            fileBackedTasksManager.taskId = tasksSortedById.last().getId() + 1;
            HistoryManager historyManager = fileBackedTasksManager.getHistoryManager();
            for (Integer taskId : history) {
                if (fileBackedTasksManager.subTasks.containsKey(taskId)) {
                    historyManager.add(fileBackedTasksManager.subTasks.get(taskId));
                } else if (fileBackedTasksManager.epics.containsKey(taskId)) {
                    historyManager.add(fileBackedTasksManager.epics.get(taskId));
                } else {
                    historyManager.add(fileBackedTasksManager.tasks.get(taskId));
                }
            }
        } catch (IOException e) {
            System.out.println("Не найден файл сохранения");
        }
        return fileBackedTasksManager;
    }

    private static List<Integer> historyFromString(String value) {
        String[] values = value.split(",");
        List<Integer> tasks = new LinkedList<>();
        for (String s : values) {
            tasks.add(Integer.parseInt(s));
        }
        return tasks;
    }

    private static Task fromString(String value) {
        if (value.isEmpty()) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String[] taskDescription = value.split(","); //id,type,name,status,description,startTime,endTime,duration,epic
        TypeOfTasks type = TypeOfTasks.valueOf(taskDescription[1]);
        if (!taskDescription[5].equals("null")) {
            switch (type) {
                case TASK:
                    return new Task(
                            Integer.parseInt(taskDescription[0]),
                            taskDescription[2],
                            Status.valueOf(taskDescription[3]),
                            taskDescription[4],
                            LocalDateTime.parse(taskDescription[5], formatter),
                            Duration.ofMinutes(Long.parseLong(taskDescription[7]))
                    );
                case EPIC:
                    return new Epic(
                            Integer.parseInt(taskDescription[0]),
                            taskDescription[2],
                            Status.valueOf(taskDescription[3]),
                            taskDescription[4],
                            LocalDateTime.parse(taskDescription[5], formatter),
                            Duration.ofMinutes(Long.parseLong(taskDescription[7]))
                    );
                case SUBTASK:
                    return new SubTask(
                            Integer.parseInt(taskDescription[0]),
                            taskDescription[2],
                            Status.valueOf(taskDescription[3]),
                            taskDescription[4],
                            LocalDateTime.parse(taskDescription[5], formatter),
                            Duration.ofMinutes(Long.parseLong(taskDescription[7])),
                            Integer.parseInt(taskDescription[8])
                    );
                default:
                    System.out.println("Неверный тип задачи");
            }

        } else {
            switch (type) {
                case TASK:
                    return new Task(
                            Integer.parseInt(taskDescription[0]),
                            taskDescription[2],
                            Status.valueOf(taskDescription[3]),
                            taskDescription[4]
                    );
                case EPIC:
                    return new Epic(
                            Integer.parseInt(taskDescription[0]),
                            taskDescription[2],
                            Status.valueOf(taskDescription[3]),
                            taskDescription[4]
                    );
                case SUBTASK:
                    return new SubTask(
                            Integer.parseInt(taskDescription[0]),
                            taskDescription[2],
                            Status.valueOf(taskDescription[3]),
                            taskDescription[4],
                            Integer.parseInt(taskDescription[8])
                    );
                default:
                    System.out.println("Неверный тип задачи");
            }
        }
        return null;
    }

    private static String historyToString(HistoryManager manager) throws IOException {
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

            bufferedWriter.write(SAVE_TITLE + "\n");

            for (Task task : getAllTasks()) {
                bufferedWriter.write(task.toString() + "\n");
            }
            for (Task epic : getAllEpics()) {
                bufferedWriter.write(epic.toString() + "\n");
            }
            for (Task subTask : getAllSubTasks()) {
                bufferedWriter.write(subTask.toString() + "\n");
            }

            bufferedWriter.write("\n");
            try {
                bufferedWriter.write(historyToString(getHistoryManager()));
            } catch (IOException e) {
                System.out.println("Нет истории");
                bufferedWriter.write("");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла");
        }
        System.out.println("Сохранение прошло успешно");
    }

    @Override
    public Task getTask(Integer taskId) {
        Task task = super.getTask(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpic(Integer taskId) {
        Epic epic = super.getEpic(taskId);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(Integer taskId) {
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


    @Override
    public String toString() {
        return "FileBackedTasksManager{" +
                "tasks=" + tasks +
                ", epics=" + epics +
                ", subTasks=" + subTasks +
                ", sortedTasks=" + sortedTasks +
                ", sortedSubTasks=" + sortedSubTasks +
                ", sortedTasksAndSubTasks=" + sortedTasksAndSubTasks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
