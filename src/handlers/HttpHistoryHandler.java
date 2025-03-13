package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HttpHistoryHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager tm;

    public HttpHistoryHandler(TaskManager tm) {
        this.tm = tm;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod();
        if (httpMethod.equals("GET")) {
            List<Task> historyList = tm.getHistory();
            String jsonHistoryList = jsonMapper.toJson(historyList);
            sendText(exchange, jsonHistoryList, 200);
        } else {
            sendNotFound(exchange, 405);
        }
    }
}
