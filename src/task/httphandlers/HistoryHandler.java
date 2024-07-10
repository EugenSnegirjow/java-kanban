package task.httphandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import task.manager.service.taskManager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            if (method.equals("GET")) {
                String response = gson.toJson(manager.getManagerHistory());
                sendText(exchange, response);
            } else {
                sendWrongMethod(exchange);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
