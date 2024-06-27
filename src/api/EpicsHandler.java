package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.ValidationException;
import manager.task.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static api.Utils.getGson;
import static api.Utils.getId;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            Endpoint endpoint = getEpicsEndpoint(path, exchange.getRequestMethod());
            Optional<Integer> id = getId(path);

            switch (endpoint) {
                case GET_EPICS:
                    try {
                        List<Epic> epics = manager.getAllEpics();
                        sendText(exchange, 200, gson.toJson(epics));
                        break;
                    } catch (RuntimeException exception) {
                        sendInternalError(exchange, exception);
                        break;
                    }
                case GET_EPICS_BY_ID:
                    try {
                        if (id.isPresent()) {
                            try {
                                Epic epic = manager.getEpic(id.get());
                                sendText(exchange, 200, gson.toJson(epic));
                                break;
                            } catch (NotFoundException exception) {
                                sendNotFound(exchange, exception);
                                break;
                            }
                        } else {
                            sendBadRequest(exchange, "id указан неверно или null");
                        }
                        break;
                    } catch (RuntimeException exception) {
                        sendInternalError(exchange, exception);
                    }
                case GET_EPICS_SUBTASKS:
                    try {
                        if (id.isPresent()) {
                            try {
                                Epic epic = manager.getEpic(id.get());
                                List<Subtask> epicSubtasks = manager.getEpicSubTasks(epic);
                                sendText(exchange, 200, gson.toJson(epicSubtasks));
                                break;
                            } catch (NotFoundException exception) {
                                sendNotFound(exchange, exception);
                                break;
                            }
                        }
                    } catch (RuntimeException exception) {
                        sendInternalError(exchange, exception);
                        break;
                    }
                case POST_EPICS:
                    try (InputStream inputStream = exchange.getRequestBody()) {
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Epic epicFromJson = gson.fromJson(body, Epic.class);
                        Epic epicCorrect = new Epic(epicFromJson.getName(), epicFromJson.getDescription());

                        if (epicFromJson.getId() != null) {
                            epicCorrect.setId(epicFromJson.getId());
                        }

                        if (id.isEmpty()) {
                            try {
                                manager.createEpic(epicCorrect);
                                sendText(exchange, 201, "Эпик успешно создан");
                            } catch (ValidationException exception) {
                                sendHasInteractions(exchange, exception);
                                break;
                            }
                            break;
                        }

                        try {
                            if (epicFromJson.getId().equals(id.get())) {
                                manager.updateEpic(epicCorrect);
                                sendText(exchange, 201, "Эпик успешно обновлен");
                            } else {
                                sendBadRequest(exchange, "id указан неверно или null");
                            }
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
                case DELETE_EPICS:
                    try {
                        if (id.isPresent()) {
                            manager.deleteEpic(id.get());
                            sendText(exchange, 204, "");
                            break;
                        }
                        sendNotFound(exchange, new NotFoundException("Эпик не найден"));
                        break;
                    } catch (NotFoundException exception) {
                        sendNotFound(exchange, exception);
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