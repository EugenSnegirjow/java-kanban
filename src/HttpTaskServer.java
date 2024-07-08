import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import task.httphandlers.*;
import task.manager.service.Managers;
import task.manager.service.taskManager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class HttpTaskServer {

    private final int port = 8080;

    private final HttpServer httpServer;
    private final Gson gson;
    private final TaskManager taskManager;
    private final TaskHandler taskHandler;
    private final EpicHandler epicHandler;
    private final SubtaskHandler subtaskHandler;
    private final HistoryHandler historyHandler;
    private final PrioritizedHandler prioritizedHandler;
    private final HttpHandler badRequestHandler;

    private final String hostName = "localhost";

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.start();
        taskServer.stop(0);
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        taskHandler = new TaskHandler(gson, taskManager);
        epicHandler = new EpicHandler(gson, taskManager);
        subtaskHandler = new SubtaskHandler(gson, taskManager);
        historyHandler = new HistoryHandler(gson, taskManager);
        prioritizedHandler = new PrioritizedHandler(gson, taskManager);
        badRequestHandler = exchange -> {
            exchange.sendResponseHeaders(400, 0);
            String response = "Неверный запрос";
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        };
        httpServer = HttpServer.create(new InetSocketAddress(hostName, port), 0);
        httpServer.createContext("/api/v1/task-manager", this::mangerHandler);
    }

    private void mangerHandler(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();

            if (Pattern.matches("^/api/v1/task-manager/tasks$", path)
                    || Pattern.matches("^/api/v1/task-manager/tasks/\\d+$", path)) {
                taskHandler.handle(exchange);
            } else if (Pattern.matches("^/api/v1/task-manager/subtasks$", path)
                    || Pattern.matches("^/api/v1/task-manager/subtasks/\\d+$", path)) {
                subtaskHandler.handle(exchange);
            } else if (Pattern.matches("^/api/v1/task-manager/epics$", path)
                    || Pattern.matches("^/api/v1/task-manager/epics/\\d+$", path)) {
                epicHandler.handle(exchange);
            } else if (Pattern.matches("^/api/v1/task-manager/history$", path)) {
                historyHandler.handle(exchange);
            } else if (Pattern.matches("^/api/v1/task-manager/prioritized$", path)) {
                prioritizedHandler.handle(exchange);
            } else {
                badRequestHandler.handle(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }


    public void start() {
        httpServer.start();
        System.out.println("Сервер запущен на порту: " + port);
        System.out.println("http://" + hostName + ":" + port + "/api/v1/task-manger");
    }

    public void stop(int delay) throws InterruptedException {
        Thread.sleep(delay);
        httpServer.stop(0);
        System.out.println("Сервер остановлен на порту: " + port);

    }
}
