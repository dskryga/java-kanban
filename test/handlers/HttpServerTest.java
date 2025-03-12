package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


public class HttpServerTest {


    TaskManager tm = Managers.getDefault();
    HttpServer httpServer;
    Gson jsonMapper = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    void init() throws IOException {

        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 8080);

        httpServer = HttpServer.create(inetSocketAddress, 8080);
        httpServer.createContext("/tasks", new HttpTasksHandler(tm));
        httpServer.createContext("/subtasks", new HttpSubTasksHandler(tm));
        httpServer.createContext("/epics", new HttpEpicHandler(tm));
        httpServer.createContext("/history", new HttpHistoryHandler(tm));
        httpServer.createContext("/prioritized", new HttpPrioritizedHandler(tm));
        tm.clearTaskList();
        tm.clearEpicList();
        tm.clearSubTaskList();
        httpServer.start();
    }

    @AfterEach
    void close() {
        httpServer.stop(0);
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("task", "taskDesc", Status.NEW);
        String taskJson = jsonMapper.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task createdTask = tm.getTaskById(1);

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(task.getName(), createdTask.getName());
        Assertions.assertEquals(task.getDescription(), createdTask.getDescription());
        Assertions.assertEquals(task.getStatus(), createdTask.getStatus());
    }

    @Test
    void testAddTaskButCrossedTime() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1Desc", Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.of(10, ChronoUnit.MINUTES));
        Task task2 = new Task("task2", "task2Desc", Status.DONE);
        task2.setStartTime(LocalDateTime.now());
        task2.setDuration(Duration.of(10, ChronoUnit.MINUTES));
        tm.addTask(task1);
        String jsonTaskToAdd = jsonMapper.toJson(task2);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonTaskToAdd)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task oldTask = new Task("oldName", "oldDesc", Status.NEW);
        tm.addTask(oldTask);
        Task newTask = new Task("newName", "newDesc", Status.DONE);
        newTask.setId(1);
        String jsonNewTask = jsonMapper.toJson(newTask);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonNewTask)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task createdTask = tm.getTaskById(1);

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(newTask, createdTask);
    }

    @Test
    void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "EpicDesc");
        tm.addEpic(epic);
        SubTask subTask = new SubTask("subTask", "subTaskDesc", Status.NEW, 1);
        String jsonSubTask = jsonMapper.toJson(subTask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonSubTask)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask createdSubTask = tm.getSubTaskById(2);

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(subTask.getName(), createdSubTask.getName());
        Assertions.assertEquals(subTask.getDescription(), createdSubTask.getDescription());
        Assertions.assertEquals(subTask.getStatus(), createdSubTask.getStatus());
    }

    @Test
    void testAddSubTaskButCrossedTime() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic");
        tm.addEpic(epic);
        SubTask subTask = new SubTask("sb1", "sb1Desc", Status.DONE, 1);
        subTask.setStartTime(LocalDateTime.now());
        subTask.setDuration(Duration.of(10, ChronoUnit.MINUTES));
        tm.addSubTask(subTask);
        SubTask subTask2 = new SubTask("sb2", "sb2Desc", Status.NEW, 1);
        subTask2.setStartTime(LocalDateTime.now());
        subTask2.setDuration(Duration.of(10, ChronoUnit.MINUTES));
        String jsonSubTask2 = jsonMapper.toJson(subTask2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonSubTask2)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
    }

    @Test
    void testUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "EpicDesc");
        tm.addEpic(epic);
        SubTask oldSubTask = new SubTask("oldName", "oldDesc", Status.NEW, 1);
        tm.addSubTask(oldSubTask);
        SubTask newSubTask = new SubTask("newName", "newDesc", Status.DONE, 1);
        newSubTask.setId(2);
        String jsonNewSubTask = jsonMapper.toJson(newSubTask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonNewSubTask)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask createdSubTask = tm.getSubTaskById(2);

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(newSubTask, createdSubTask);
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("name", "desc");
        String jsonEpic = jsonMapper.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonEpic)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic createdEpic = tm.getEpicById(1);

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(epic.getName(), createdEpic.getName());
        Assertions.assertEquals(epic.getDescription(), createdEpic.getDescription());
    }

    @Test
    void testUpdateEpic() throws IOException, InterruptedException {
        Epic oldEpic = new Epic("oldName", "oldDesc");
        tm.addEpic(oldEpic);
        Epic newEpic = new Epic("newName", "newDesc");
        newEpic.setId(1);
        String jsonNewEpic = jsonMapper.toJson(newEpic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonNewEpic)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic createdEpic = tm.getEpicById(1);

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(newEpic, createdEpic);
    }

    @Test
    void testGetTaskList() throws IOException, InterruptedException {
        Task task1 = new Task("a", "AA", Status.NEW);
        tm.addTask(task1);
        Task task2 = new Task("b", "BB", Status.DONE);
        tm.addTask(task2);
        List<Task> taskList = tm.showTaskList();
        String jsonTaskList = jsonMapper.toJson(taskList);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonCreatedList = response.body();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(jsonTaskList, jsonCreatedList);
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("name", "desc", Status.DONE);
        tm.addTask(task);
        String jsonTask = jsonMapper.toJson(tm.getTaskById(1));

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonCreatedTask = response.body();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(jsonTask, jsonCreatedTask);
    }

    @Test
    void testGetTaskByIdButNotFound() throws IOException, InterruptedException {
        Task task = new Task("name", "desc", Status.DONE);
        tm.addTask(task);
        URI url = URI.create("http://localhost:8080/tasks/121312");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void testGetSubTaskList() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc");
        tm.addEpic(epic);
        SubTask subTask1 = new SubTask("sb1", "sb1", Status.DONE, 1);
        tm.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("sb2", "sb2", Status.NEW, 1);
        tm.addSubTask(subTask2);
        List<SubTask> subTaskList = tm.showSubTaskList();
        String jsonSubTaskList = jsonMapper.toJson(subTaskList);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonCreatedList = response.body();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(jsonSubTaskList, jsonCreatedList);
    }

    @Test
    void testGetSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc");
        tm.addEpic(epic);
        SubTask subTask1 = new SubTask("sb1", "sb1", Status.DONE, 1);
        tm.addSubTask(subTask1);
        String jsonSubTask = jsonMapper.toJson(tm.getSubTaskById(2));
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonCreatedSubTask = response.body();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(jsonSubTask, jsonCreatedSubTask);
    }

    @Test
    void testGetSubTaskByIdButNotFound() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc");
        tm.addEpic(epic);
        SubTask subTask1 = new SubTask("sb1", "sb1", Status.DONE, 1);
        tm.addSubTask(subTask1);
        URI url = URI.create("http://localhost:8080/subtasks/2312412");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc");
        tm.addEpic(epic);
        List<Epic> epicList = tm.showEpicList();
        String jsonEpicList = jsonMapper.toJson(epicList);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonCreatedList = response.body();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(jsonEpicList, jsonCreatedList);
    }

    @Test
    void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc");
        tm.addEpic(epic);
        String jsonEpic = jsonMapper.toJson(tm.getEpicById(1));
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonCreatedEpic = response.body();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(jsonEpic, jsonCreatedEpic);
    }

    @Test
    void testGetEpicByIdButNotFound() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc");
        tm.addEpic(epic);
        URI url = URI.create("http://localhost:8080/epics/11532535");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void testGetEpicSubTasksIds() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc");
        tm.addEpic(epic);
        SubTask subTask1 = new SubTask("sb1", "sb1", Status.DONE, 1);
        tm.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("sb2", "sb2", Status.NEW, 1);
        tm.addSubTask(subTask2);
        List<SubTask> subTaskList = tm.showSubTaskListByEpicId(1);
        String jsonSubTaskList = jsonMapper.toJson(subTaskList);
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonCreatedSubTasksList = response.body();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(jsonSubTaskList, jsonCreatedSubTasksList);
    }

    @Test
    void testGetEpicSubTasksIdsButNotFound() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc");
        tm.addEpic(epic);
        SubTask subTask1 = new SubTask("sb1", "sb1", Status.DONE, 1);
        tm.addSubTask(subTask1);
        URI url = URI.create("http://localhost:8080/epics/1213123/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void removeTaskList() throws IOException, InterruptedException {
        Task task = new Task("name", "desc", Status.NEW);
        tm.addTask(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> taskList = tm.showTaskList();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(taskList.isEmpty());
    }

    @Test
    void removeTaskById() throws IOException, InterruptedException {
        Task task = new Task("name", "desc", Status.NEW);
        tm.addTask(task);
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> taskList = tm.showTaskList();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(taskList.isEmpty());
    }

    @Test
    void removeSubTaskList() throws IOException, InterruptedException {
        Epic epic = new Epic("dsad", "dsds");
        tm.addEpic(epic);
        SubTask task = new SubTask("name", "desc", Status.NEW, 1);
        tm.addSubTask(task);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> taskList = tm.showSubTaskList();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(taskList.isEmpty());
    }

    @Test
    void removeSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("dsad", "dsds");
        tm.addEpic(epic);
        SubTask task = new SubTask("name", "desc", Status.NEW, 1);
        tm.addSubTask(task);
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> taskList = tm.showSubTaskList();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(taskList.isEmpty());
    }

    @Test
    void removeEpicList() throws IOException, InterruptedException {
        Epic epic = new Epic("name", "desc");
        tm.addEpic(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicList = tm.showEpicList();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(epicList.isEmpty());
    }

    @Test
    void removeEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("name", "desc");
        tm.addEpic(epic);
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicList = tm.showEpicList();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(epicList.isEmpty());
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1Desc", Status.NEW);
        Task task2 = new Task("task2", "task2Desc", Status.DONE);
        tm.addTask(task1);
        tm.addTask(task2);
        tm.getTaskById(2);
        tm.getTaskById(1);
        List<Task> historyList = tm.getHistory();
        String jsonHistoryList = jsonMapper.toJson(historyList);
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonCreatedHistoryList = response.body();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(jsonHistoryList, jsonCreatedHistoryList);
    }

    @Test
    void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1Desc", Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.of(10, ChronoUnit.MINUTES));
        Task task2 = new Task("task2", "task2Desc", Status.DONE);
        task2.setStartTime(LocalDateTime.now().minus(10, ChronoUnit.DAYS));
        task2.setDuration(Duration.of(10, ChronoUnit.MINUTES));
        tm.addTask(task1);
        tm.addTask(task2);
        List<Task> prioritizedTasks = tm.getPrioritizedTasks();
        String jsonPrioritizedTasks = jsonMapper.toJson(prioritizedTasks);
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonCreatedPrioritizedTasks = response.body();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(jsonPrioritizedTasks, jsonCreatedPrioritizedTasks);
    }


}


