package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HttpPrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager tm;

    public HttpPrioritizedHandler(TaskManager tm) {
        this.tm = tm;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod();
        if (httpMethod.equals("GET")) {
            List<Task> prioritizedList = tm.getPrioritizedTasks();
            String jsonPrioritizedList = jsonMapper.toJson(prioritizedList);
            sendText(exchange, jsonPrioritizedList, 200);
        } else {
            sendNotFound(exchange, 405);
        }
    }
}

