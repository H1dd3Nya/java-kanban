package api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.ValidationException;
import manager.task.TaskManager;
import model.Endpoint;
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

            try {
                switch (endpoint) {
                    case GET_EPICS:
                        getEpic(exchange);
                        break;
                    case GET_EPICS_BY_ID:
                        if (id.isPresent()) {
                            getEpicById(exchange, id.get());
                            break;
                        }

                        sendBadRequest(exchange, "id указан неверно или null");
                        break;
                    case GET_EPICS_SUBTASKS:
                        if (id.isPresent()) {
                            getEpicSubtasks(exchange, id.get());
                            break;
                        }

                        sendBadRequest(exchange, "id указан неверно или null");
                        break;
                    case POST_EPICS:
                        if (id.isPresent()) {
                            postUpdateEpic(exchange, id.get());
                            break;
                        }

                        postCreateEpic(exchange);
                        break;
                    case DELETE_EPICS:
                        if (id.isPresent()) {
                            deleteEpic(exchange, id.get());
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

    private Endpoint getEpicsEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 4) {
            return Endpoint.GET_EPICS_SUBTASKS;
        }

        if (requestMethod.equals("GET") && pathParts.length == 2) {
            return Endpoint.GET_EPICS;
        } else if (requestMethod.equals("GET") && pathParts.length == 3) {
            return Endpoint.GET_EPICS_BY_ID;
        } else {
            switch (requestMethod) {
                case "POST":
                    return Endpoint.POST_EPICS;
                case "DELETE":
                    return Endpoint.DELETE_EPICS;
            }
        }

        return Endpoint.UNKNOWN;
    }

    private void getEpic(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getAllEpics();
        sendText(exchange, 200, gson.toJson(epics));
    }

    private void getEpicById(HttpExchange exchange, Integer id) throws IOException {
        Epic epic = manager.getEpic(id);
        sendText(exchange, 200, gson.toJson(epic));
    }

    private void getEpicSubtasks(HttpExchange exchange, Integer id) throws IOException {
        Epic epic = manager.getEpic(id);
        List<Subtask> epicSubtasks = manager.getEpicSubTasks(epic);
        sendText(exchange, 200, gson.toJson(epicSubtasks));
    }

    private void postCreateEpic(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Epic epicFromJson = gson.fromJson(body, Epic.class);

            manager.createEpic(epicFromJson);
            sendText(exchange, 201, "Эпик успешно создан");
        }
    }

    private void postUpdateEpic(HttpExchange exchange, Integer id) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Epic epicFromJson = gson.fromJson(body, Epic.class);
            Epic epicCorrect = new Epic(epicFromJson.getName(), epicFromJson.getDescription());

            if (!epicFromJson.getId().equals(id)) {
                sendBadRequest(exchange, "id указан неверно или null");
            }

            if (epicFromJson.getId() != null) {
                epicCorrect.setId(epicFromJson.getId());
            }

            manager.updateEpic(epicCorrect);
            sendText(exchange, 201, "Эпик успешно обновлен");
        }
    }

    private void deleteEpic(HttpExchange exchange, Integer id) throws IOException {
        manager.deleteEpic(id);
        sendText(exchange, 204, "");
    }
}