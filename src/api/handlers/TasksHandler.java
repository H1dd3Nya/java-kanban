package api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.ValidationException;
import manager.task.TaskManager;
import model.Endpoint;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static api.Utils.getGson;
import static api.Utils.getId;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            Endpoint endpoint = getTasksEndpoint(path, exchange.getRequestMethod());
            Optional<Integer> id = getId(path);

            try {
                switch (endpoint) {
                    case GET_TASKS:
                        getTasks(exchange);
                        break;
                    case GET_TASKS_BY_ID:
                        if (id.isPresent()) {
                            getTaskById(exchange, id.get());
                            break;
                        }

                        sendBadRequest(exchange, "id указан неверно или null");
                        break;
                    case POST_TASKS:
                        if (id.isPresent()) {
                            postUpdateTask(exchange, id.get());
                            break;
                        }

                        postCreateTask(exchange);
                        break;
                    case DELETE_TASKS:
                        if (id.isPresent()) {
                            deleteTask(exchange, id.get());
                            break;
                        }

                        sendBadRequest(exchange, "id указан неверно или null");
                        break;
                    case UNKNOWN:
                        sendBadRequest(exchange, "Не найден указанный эндпоинт");
                }
            } catch (ValidationException exception) {
                sendHasInteractions(exchange, exception);
            } catch (NotFoundException exception) {
                sendNotFound(exchange, exception);
            } catch (RuntimeException exception) {
                sendInternalError(exchange, exception);
            }
        }
    }

    private Endpoint getTasksEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET") && pathParts.length == 2) {
            return Endpoint.GET_TASKS;
        } else if (requestMethod.equals("GET") && pathParts.length == 3) {
            return Endpoint.GET_TASKS_BY_ID;
        } else {
            switch (requestMethod) {
                case "POST":
                    return Endpoint.POST_TASKS;
                case "DELETE":
                    return Endpoint.DELETE_TASKS;
            }
        }

        return Endpoint.UNKNOWN;
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getAllTasks();
        sendText(exchange, 200, gson.toJson(tasks));
    }

    private void getTaskById(HttpExchange exchange, Integer id) throws IOException {
        Task task = manager.getTask(id);
        sendText(exchange, 200, gson.toJson(task));
    }

    private void postCreateTask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task taskFromJson = gson.fromJson(body, Task.class);

            manager.createTask(taskFromJson);
            sendText(exchange, 201, "Задача успешно создана");
        }
    }

    private void postUpdateTask(HttpExchange exchange, Integer id) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task taskFromJson = gson.fromJson(body, Task.class);

            if (!taskFromJson.getId().equals(id)) {
                sendBadRequest(exchange, "id указан неверно или null");
                return;
            }

            manager.updateTask(taskFromJson);
            sendText(exchange, 201, "Задача успешно обновлена");
        }
    }

    private void deleteTask(HttpExchange exchange, Integer id) throws IOException {
        manager.deleteTask(id);
        sendText(exchange, 204, "");
    }
}