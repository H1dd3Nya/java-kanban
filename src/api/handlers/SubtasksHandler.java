package api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.ValidationException;
import manager.task.TaskManager;
import model.Endpoint;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static api.Utils.getGson;
import static api.Utils.getId;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            Endpoint endpoint = getSubtasksEndpoint(path, exchange.getRequestMethod());
            Optional<Integer> id = getId(path);

            switch (endpoint) {
                case GET_SUBTASKS:
                    try {
                        List<Subtask> subtasks = manager.getAllSubtasks();
                        sendText(exchange, 200, gson.toJson(subtasks));
                        break;
                    } catch (RuntimeException exception) {
                        sendInternalError(exchange, exception);
                        break;
                    }
                case GET_SUBTASKS_BY_ID:
                    try {
                        if (id.isPresent()) {
                            try {
                                Subtask subtask = manager.getSubTask(id.get());
                                sendText(exchange, 200, gson.toJson(subtask));
                                break;
                            } catch (NotFoundException exception) {
                                sendNotFound(exchange, exception);
                                break;
                            }
                        }
                        sendBadRequest(exchange, "id указан неверно или null");
                    } catch (RuntimeException exception) {
                        sendInternalError(exchange, exception);
                        break;
                    }
                case POST_SUBTASKS:
                    try (InputStream inputStream = exchange.getRequestBody()) {

                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Subtask subtaskFromJson = gson.fromJson(body, Subtask.class);

                        if (id.isEmpty()) {
                            try {
                                manager.createSubTask(subtaskFromJson);
                                sendText(exchange, 201, "Подзадача успешно создана");
                            } catch (ValidationException exception) {
                                sendHasInteractions(exchange, exception);
                            } catch (NotFoundException exception) {
                                sendNotFound(exchange, exception);
                            }
                            break;
                        }

                        try {
                            if (subtaskFromJson.getId().equals(id.get())) {
                                manager.updateSubTask(subtaskFromJson);
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
                        break;
                    }

                case DELETE_SUBTASKS:
                    try {
                        if (id.isPresent()) {
                            manager.deleteSubTask(id.get());
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