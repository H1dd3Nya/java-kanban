import manager.HttpTaskServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            HttpTaskServer httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}