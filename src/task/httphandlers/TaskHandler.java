package task.httphandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import task.manager.model.Task;
import task.manager.service.exception.NotFoundException;
import task.manager.service.exception.WrongTaskException;
import task.manager.service.taskManager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET": {
                if (Pattern.matches("^/api/v1/task-manager/tasks$", path)) {
                    String response = gson.toJson(manager.getAllTasks());
                    sendText(exchange, response);
                } else if (Pattern.matches("^/api/v1/task-manager/tasks/\\d+$", path)) {
                    String pathId = path.replaceFirst("/api/v1/task-manager/tasks/", "");
                    int id = parsePathId(pathId);
                    if (id != -1) {
                        try {
                            String response = gson.toJson(manager.getTask(id));
                            sendText(exchange, response);
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        }
                    } else {
                        sendBadRequest(exchange, "Id задачи указан некорректно");
                    }
                } else {
                    sendBadRequest(exchange, "Id задачи не указан");
                }
                break;
            }
            case "POST":{
                String request = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(request, Task.class);

                if (Pattern.matches("^/api/v1/task-manager/tasks$", path)) {
                    try {
                        manager.create(task);
                        String response = "Задача успешно добавлена";
                        sendPostSuccess(exchange, response);
                    } catch (WrongTaskException e) {
                        sendHasInteractions(exchange, e.getMessage());
                    }
                } else if (Pattern.matches("^/api/v1/task-manager/tasks/\\d+$", path)) {
                    String pathId = path.replaceFirst("/api/v1/task-manager/tasks/", "");
                    int id = parsePathId(pathId);
                    if (id != -1) {
                        try {
                            manager.update(task);
                            String response = "Задача успешно обновлена";
                            sendPostSuccess(exchange, response);
                        }  catch (NotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        }  catch (WrongTaskException e) {
                            sendHasInteractions(exchange, e.getMessage());
                        }
                    } else {
                        sendBadRequest(exchange, "Id задачи указан некорректно");
                    }
                }
                break;
            }
            case "DELETE": {
                if (Pattern.matches("^/api/v1/task-manager/tasks/\\d+$", path)) {
                    String pathId = path.replaceFirst("/api/v1/task-manager/tasks/", "");
                    int id = parsePathId(pathId);
                    if (id != -1) {
                        try {
                            manager.removeTask(id);
                            sendText(exchange, "Задача с id " + id + " успешно удалена");
                        } catch (NotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        }
                    } else {
                        sendBadRequest(exchange, "Id задачи указан некорректно");
                    }
                } else {
                    sendBadRequest(exchange, "Id задачи не указан");
                }
                break;
            }
            default: sendWrongMethod(exchange);
            }
        }
    }

