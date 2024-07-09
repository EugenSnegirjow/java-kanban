package task.httphandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import task.manager.model.Epic;
import task.manager.service.exception.NotFoundException;
import task.manager.service.exception.WrongTaskException;
import task.manager.service.taskManager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET": {
                    if (Pattern.matches("^/api/v1/task-manager/epics$", path)) {
                        String response = gson.toJson(manager.getAllEpics());
                        sendText(exchange, response);
                    } else if (Pattern.matches("^/api/v1/task-manager/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/task-manager/epics/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            try {
                                String response = gson.toJson(manager.getEpic(id));
                                sendText(exchange, response);
                            } catch (NotFoundException e) {
                                sendNotFound(exchange, e.getMessage());
                            }
                        } else {
                            sendBadRequest(exchange, "Id эпика указан некорректно");
                        }
                    } else {
                        sendBadRequest(exchange, "Id эпика не указан");
                    }
                    break;
                }
                case "POST": {
                    String request = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic task = gson.fromJson(request, Epic.class);

                    if (Pattern.matches("^/api/v1/task-manager/epics$", path)) {
                        try {
                            manager.create(task);
                            String response = "Эпик успешно добавлена";
                            sendPostSuccess(exchange, response);
                        } catch (WrongTaskException e) {
                            sendHasInteractions(exchange, e.getMessage());
                        }
                    } else if (Pattern.matches("^/api/v1/task-manager/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/task-manager/epics/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            try {
                                manager.update(task);
                                String response = "Эпик успешно обновлена";
                                sendPostSuccess(exchange, response);
                            } catch (NotFoundException e) {
                                sendNotFound(exchange, e.getMessage());
                            } catch (WrongTaskException e) {
                                sendHasInteractions(exchange, e.getMessage());
                            }
                        } else {
                            sendBadRequest(exchange, "Id эпика указан некорректно");
                        }
                    }
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/api/v1/task-manager/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/task-manager/epics/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            try {
                                manager.removeEpic(id);
                                sendText(exchange, "Эпик с id " + id + " успешно удалена");
                            } catch (NotFoundException e) {
                                sendNotFound(exchange, e.getMessage());
                            }
                        } else {
                            sendBadRequest(exchange, "Id эпик указан некорректно");
                        }
                    } else {
                        sendBadRequest(exchange, "Id эпик не указан");
                    }
                    break;
                }
                default:
                    sendWrongMethod(exchange);
            }
        } catch (IOException e) {
            sendUnknownError(exchange);
        }
    }

}
