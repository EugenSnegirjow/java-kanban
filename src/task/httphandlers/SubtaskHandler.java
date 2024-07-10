package task.httphandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import task.manager.model.SubTask;
import task.manager.service.exception.NotFoundException;
import task.manager.service.exception.WrongTaskException;
import task.manager.service.taskManager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET": {
                    if (Pattern.matches("^/api/v1/task-manager/subtasks$", path)) {
                        String response = gson.toJson(manager.getAllSubTasks());
                        sendText(exchange, response);
                    } else if (Pattern.matches("^/api/v1/task-manager/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/task-manager/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            try {
                                String response = gson.toJson(manager.getSubTask(id));
                                sendText(exchange, response);
                            } catch (NotFoundException e) {
                                sendNotFound(exchange, e.getMessage());
                            }
                        } else {
                            sendBadRequest(exchange, "Id подзадачи указан некорректно");
                        }
                    } else {
                        sendBadRequest(exchange, "Id подзадачи не указан");
                    }
                    break;
                }
                case "POST": {
                    String request = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    SubTask task = gson.fromJson(request, SubTask.class);

                    if (Pattern.matches("^/api/v1/task-manager/subtasks$", path)) {
                        try {
                            manager.create(task);
                            String response = "Подзадача успешно добавлена";
                            sendPostSuccess(exchange, response);
                        } catch (WrongTaskException e) {
                            sendHasInteractions(exchange, e.getMessage());
                        }
                    } else if (Pattern.matches("^/api/v1/task-manager/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/task-manager/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            try {
                                manager.update(task);
                                String response = "Подзадача успешно обновлена";
                                sendPostSuccess(exchange, response);
                            } catch (NotFoundException e) {
                                sendNotFound(exchange, e.getMessage());
                            } catch (WrongTaskException e) {
                                sendHasInteractions(exchange, e.getMessage());
                            }
                        } else {
                            sendBadRequest(exchange, "Id подзадачи указан некорректно");
                        }
                    }
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/api/v1/task-manager/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/task-manager/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            try {
                                manager.removeSubTask(id);
                                sendText(exchange, "Подзадача с id " + id + " успешно удалена");
                            } catch (NotFoundException e) {
                                sendNotFound(exchange, e.getMessage());
                            }
                        } else {
                            sendBadRequest(exchange, "Id подзадачи указан некорректно");
                        }
                    } else {
                        sendBadRequest(exchange, "Id подзадачи не указан");
                    }
                    break;
                }
                default:
                    sendWrongMethod(exchange);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

