package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.FileManagerCrossedTimeInTasksException;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.util.List;

public class HttpEpicHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager tm;

    public HttpEpicHandler(TaskManager tm) {
        this.tm = tm;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod();

        switch (httpMethod) {
            case "GET":
                getEpics(exchange);
                break;
            case "POST":
                postEpics(exchange);
                break;

            case "DELETE":
                deleteEpics(exchange);
                break;
            default:
                sendNotFound(exchange, 405);
                break;
        }
    }

    private void getEpics(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 4) {
            if (urlParts[3].equals("subtasks")) {
                try {
                    Integer id = Integer.valueOf(urlParts[2]);
                    if (tm.getEpicById(id) == null) {
                        sendNotFound(exchange, 404);
                        return;
                    }
                    List<SubTask> subTaskOfEpicList = tm.showSubTaskListByEpicId(id);
                    String jsonSubTaskOfEpicList = jsonMapper.toJson(subTaskOfEpicList);
                    sendText(exchange, jsonSubTaskOfEpicList, 200);
                } catch (NumberFormatException e) {
                    sendText(exchange, "Неподдерживаемый формат id эпика", 400);
                    return;
                }
            }
        }

        if (urlParts.length == 3) {
            try {
                Integer id = Integer.valueOf(urlParts[2]);
                Epic epic = tm.getEpicById(id);
                if (epic == null) {
                    sendNotFound(exchange, 404);
                } else {
                    String jsonEpic = jsonMapper.toJson(epic);
                    sendText(exchange, jsonEpic, 200);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Неподдерживаемый формат id эпика", 400);
                return;
            }
        }

        if (urlParts.length == 2) {
            List<Epic> allEpics = tm.showEpicList();
            String jsonAllEpics = jsonMapper.toJson(allEpics);
            sendText(exchange, jsonAllEpics, 200);
        } else {
            sendText(exchange, "Неподдерживаемый формат URL запроса", 400);
        }
    }

    private void deleteEpics(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
            try {
                Integer id = Integer.valueOf(urlParts[2]);
                tm.removeEpicById(id);
                sendText(exchange, String.format("Эпик с id %d удалена или не существует", id), 200);
            } catch (NumberFormatException e) {
                sendText(exchange, "Неподдерживаемый формат id эпика", 400);
                return;
            }
        }

        if (urlParts.length == 2) {
            tm.clearEpicList();
            sendText(exchange, "Весь список эпиков очищен", 200);
        } else {
            sendText(exchange, "Неподдерживаемый формат URL запроса", 400);
        }
    }

    private void postEpics(HttpExchange exchange) throws IOException {
        String jsonEpic = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Epic epic = jsonMapper.fromJson(jsonEpic, Epic.class);
        if (epic.getId() == null) {
            try {
                Epic createdEpic = tm.addEpic(epic);
                Integer id = createdEpic.getId();
                sendText(exchange, String.format("Эпик с id %d успешно добавлен", id), 201);
            } catch (FileManagerCrossedTimeInTasksException e) {
                sendText(exchange, e.getMessage(), 406);
            }
        } else {
            tm.updateEpic(epic);
            sendText(exchange, "Эпик успешно обновлен", 201);
        }
    }


}
