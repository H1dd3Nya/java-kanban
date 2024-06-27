package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.ValidationException;
import manager.task.TaskManager;
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

            switch (endpoint) {
                case GET_TASKS:
                    try {
                        List<Task> tasks = manager.getAllTasks();
                        sendText(exchange, 200, gson.toJson(tasks));
                        break;
                    } catch (RuntimeException exception) {

                        sendInternalError(exchange, exception);
                        break;
                    }
                case GET_TASKS_BY_ID:
                    try {

                        if (id.isPresent()) {

                            try {
                                Task task = manager.getTask(id.get());
                                sendText(exchange, 200, gson.toJson(task));
                                break;
                            } catch (NotFoundException exception) {
                                sendNotFound(exchange, exception);
                                break;
                            }

                        }

                        sendBadRequest(exchange, "id указан неверно или null");
                        break;
                    } catch (RuntimeException exception) {

                        sendInternalError(exchange, exception);
                        break;
                    }
                case POST_TASKS:
                    try (InputStream inputStream = exchange.getRequestBody()) {
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Task taskFromJson = gson.fromJson(body, Task.class);

                        if (id.isEmpty()) {
                            try {
                                manager.createTask(taskFromJson);
                                sendText(exchange, 201, "Задача успешно создана");
                                break;
                            } catch (ValidationException exception) {
                                sendHasInteractions(exchange, exception);
                            }
                            break;
                        }

                        try {

                            if (taskFromJson.getId().equals(id.get())) {
                                manager.updateTask(taskFromJson);
                                sendText(exchange, 201, "Задача успешно обновлена");
                                break;
                            }

                            sendBadRequest(exchange, "id указан неверно или null");
                            break;
                        } catch (NotFoundException exception) {

                            sendNotFound(exchange, exception);
                        } catch (ValidationException exception) {

                            sendHasInteractions(exchange, exception);
                        }

                        break;
                    } catch (RuntimeException exception) {

                        sendInternalError(exchange, exception);
                    }
                case DELETE_TASKS:
                    try {

                        if (id.isPresent()) {
                            manager.deleteTask(id.get());
                            sendText(exchange, 204, "");
                            break;
                        }

                    } catch (NotFoundException exception) {

                        sendNotFound(exchange, exception);
                        break;
                    } catch (RuntimeException exception) {

                        sendInternalError(exchange, exception);
                        break;
                    }
                case UNKNOWN:
                    sendBadRequest(exchange, "Не найден указанный эндпоинт");
            }
        }
    }
}