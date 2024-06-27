package api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.task.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

import static api.Utils.getGson;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            List<Task> history = manager.getHistory();
            sendText(exchange, 200, gson.toJson(history));
        } catch (RuntimeException exception) {
            sendInternalError(exchange, exception);
        }
    }
}