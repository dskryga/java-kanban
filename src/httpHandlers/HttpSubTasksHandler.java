package httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.FileManagerCrossedTimeInTasksException;
import manager.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.util.List;

public class HttpSubTasksHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager tm;

    public HttpSubTasksHandler(TaskManager tm) {
        this.tm = tm;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod();

        switch (httpMethod) {
            case "GET":
                getSubTasks(exchange);
                break;
            case "POST":
                postSubTasks(exchange);
                break;

            case "DELETE":
                deleteSubTasks(exchange);
                break;
            default:
                sendNotFound(exchange, 405);
                break;
        }
    }

    private void getSubTasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
            try {
                Integer id = Integer.valueOf(urlParts[2]);
                SubTask subTask = tm.getSubTaskById(id);
                if (subTask == null) {
                    sendNotFound(exchange, 404);
                } else {
                    String jsonSubTask = jsonMapper.toJson(subTask);
                    sendText(exchange, jsonSubTask, 200);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Неподдерживаемый формат id подзадачи", 400);
                return;
            }
        }

        if (urlParts.length == 2) {
            //получение всех задач
            List<SubTask> allSubTasks = tm.showSubTaskList();
            String jsonAllSubTasks = jsonMapper.toJson(allSubTasks);
            sendText(exchange, jsonAllSubTasks, 200);
        } else {
            sendText(exchange, "Неподдерживаемый формат URL запроса", 400);
        }
    }

    private void deleteSubTasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
            try {
                Integer id = Integer.valueOf(urlParts[2]);
                tm.removeSubTaskById(id);
                sendText(exchange, String.format("Подзадача с id %d удалена или не существует", id), 200);
            } catch (NumberFormatException e) {
                sendText(exchange, "Неподдерживаемый формат id подзадачи", 400);
                return;
            }
        }

        if (urlParts.length == 2) {
            tm.clearSubTaskList();
            sendText(exchange, "Весь список подзадач очищен", 200);
        } else {
            sendText(exchange, "Неподдерживаемый формат URL запроса", 400);
        }
    }

    private void postSubTasks(HttpExchange exchange) throws IOException {
        String jsonSubTask = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        SubTask subTask = jsonMapper.fromJson(jsonSubTask, SubTask.class);
        if (subTask.getId() == null) {
            try {
                tm.addSubTask(subTask);
                sendText(exchange, "Подзадача успешно добавлена", 201);
            } catch (FileManagerCrossedTimeInTasksException e) {
                sendText(exchange, e.getMessage(), 406);
            }
        } else {
            tm.updateSubTask(subTask);
            sendText(exchange, "Подзадача успешно обновлена", 201);
        }
    }

}
