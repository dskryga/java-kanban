import manager.Managers;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {
            HttpTaskServer httpTaskServer = new HttpTaskServer("localhost", 8080, Managers.getDefault());
            httpTaskServer.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
