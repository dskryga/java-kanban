package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.FileManagerCrossedTimeInTasksException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HttpTasksHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager tm;

    public HttpTasksHandler(TaskManager tm) {
        this.tm = tm;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod();

        switch (httpMethod) {
            case "GET":
                getTasks(exchange);
                break;
            case "POST":
                postTasks(exchange);
                break;

            case "DELETE":
                deleteTasks(exchange);
                break;
            default:
                sendNotFound(exchange, 405);
                break;
        }

    }

    private void getTasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
            try {
                Integer id = Integer.valueOf(urlParts[2]);
                Task task = tm.getTaskById(id);
                if (task == null) {
                    sendNotFound(exchange, 404);
                } else {
                    String jsonTask = jsonMapper.toJson(task);
                    sendText(exchange, jsonTask, 200);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Неподдерживаемый формат id задачи", 400);
                return;
            }
        }

        if (urlParts.length == 2) {
            //получение всех задач
            List<Task> allTasks = tm.showTaskList();
            String jsonAllTasks = jsonMapper.toJson(allTasks);
            sendText(exchange, jsonAllTasks, 200);
        } else {
            sendText(exchange, "Неподдерживаемый формат URL запроса", 400);
        }


    }

    private void deleteTasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
            try {
                Integer id = Integer.valueOf(urlParts[2]);
                tm.removeTaskById(id);
                sendText(exchange, String.format("Задача с id %d удалена или не существует", id), 200);
            } catch (NumberFormatException e) {
                sendText(exchange, "Неподдерживаемый формат id задачи", 400);
                return;
            }
        }

        if (urlParts.length == 2) {
            tm.clearTaskList();
            sendText(exchange, "Весь список задач очищен", 200);
        } else {
            sendText(exchange, "Неподдерживаемый формат URL запроса", 400);
        }
    }

    private void postTasks(HttpExchange exchange) throws IOException {
        String jsonTask = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Task task = jsonMapper.fromJson(jsonTask, Task.class);
        if (task.getId() == null) {
            try {
                Task createdTask = tm.addTask(task);
                Integer id = createdTask.getId();
                sendText(exchange, String.format("Задача с id %d успешно добавлена", id), 201);
            } catch (FileManagerCrossedTimeInTasksException e) {
                sendText(exchange, e.getMessage(), 406);
            }
        } else {
            tm.updateTask(task);
            sendText(exchange, "Задача успешно обновлена", 201);
        }
    }
}
