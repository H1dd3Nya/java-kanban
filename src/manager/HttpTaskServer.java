package manager;

import api.handlers.*;
import com.sun.net.httpserver.HttpServer;
import manager.task.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        this.manager = Managers.getDefault();
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public void start() throws IOException {
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
        httpServer.createContext("/tasks", new TasksHandler(manager));
        httpServer.createContext("/subtasks", new SubtasksHandler(manager));
        httpServer.createContext("/epics", new EpicsHandler(manager));

        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}