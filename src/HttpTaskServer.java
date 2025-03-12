import com.sun.net.httpserver.HttpServer;
import httpHandlers.*;
import manager.Managers;
import manager.TaskManager;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class HttpTaskServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {

        TaskManager tm = Managers.getDefault();

        Task task = new Task("task1", "task1Desc", Status.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.of(10, ChronoUnit.MINUTES));
        tm.addTask(task);

        Task task1 = new Task("earlier task", "desc", Status.NEW);
        task1.setStartTime(LocalDateTime.now().minus(10, ChronoUnit.DAYS));
        task1.setDuration(Duration.of(10, ChronoUnit.MINUTES));
        tm.addTask(task1);


        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", PORT);
        HttpServer httpServer = HttpServer.create(inetSocketAddress, 0);

        httpServer.createContext("/tasks", new HttpTasksHandler(tm));
        httpServer.createContext("/subtasks", new HttpSubTasksHandler(tm));
        httpServer.createContext("/epics", new HttpEpicHandler(tm));
        httpServer.createContext("/history", new HttpHistoryHandler(tm));
        httpServer.createContext("/prioritized", new HttpPrioritizedHandler(tm));

        httpServer.start();
    }
}
