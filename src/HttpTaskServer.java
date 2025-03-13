import com.sun.net.httpserver.HttpServer;
import handlers.*;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final int port;
    private final String hostname;
    private TaskManager tm;
    private HttpServer httpServer;

    public HttpTaskServer(String hostname, int port, TaskManager tm) throws IOException {
        this.tm = tm;
        this.port = port;
        this.hostname = hostname;

        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port);
        httpServer = HttpServer.create(inetSocketAddress, 0);

        httpServer.createContext("/tasks", new HttpTasksHandler(tm));
        httpServer.createContext("/subtasks", new HttpSubTasksHandler(tm));
        httpServer.createContext("/epics", new HttpEpicHandler(tm));
        httpServer.createContext("/history", new HttpHistoryHandler(tm));
        httpServer.createContext("/prioritized", new HttpPrioritizedHandler(tm));
    }

    public void startServer() {
        httpServer.start();
    }

    public void stopServer(int delay) {
        httpServer.stop(delay);
    }
}
