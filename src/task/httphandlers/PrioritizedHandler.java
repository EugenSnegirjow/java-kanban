package task.httphandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import task.manager.service.taskManager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            String response = gson.toJson(manager.getPrioritizedTasksAndSubTasks());
            sendText(exchange, response);
        } else {
            sendWrongMethod(exchange);
        }
    }
}
