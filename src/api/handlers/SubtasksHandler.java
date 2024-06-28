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

            try {
                switch (endpoint) {
                    case GET_SUBTASKS:
                        getSubtasks(exchange);
                        break;
                    case GET_SUBTASKS_BY_ID:
                        if (id.isPresent()) {
                            getSubtaskById(exchange, id.get());
                            break;
                        }

                        sendBadRequest(exchange, "id указан неверно или null");
                        break;
                    case POST_SUBTASKS:
                        if (id.isPresent()) {
                            postUpdateSubtask(exchange, id.get());
                            break;
                        }

                        postCreateSubtask(exchange);
                        break;
                    case DELETE_SUBTASKS:
                        if (id.isPresent()) {
                            deleteSubtask(exchange, id.get());
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

    private Endpoint getSubtasksEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET") && pathParts.length == 2) {
            return Endpoint.GET_SUBTASKS;
        } else if (requestMethod.equals("GET") && pathParts.length == 3) {
            return Endpoint.GET_SUBTASKS_BY_ID;
        } else {
            switch (requestMethod) {
                case "POST":
                    return Endpoint.POST_SUBTASKS;
                case "DELETE":
                    return Endpoint.DELETE_SUBTASKS;
            }
        }

        return Endpoint.UNKNOWN;
    }

    private void getSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = manager.getAllSubtasks();
        sendText(exchange, 200, gson.toJson(subtasks));
    }

    private void getSubtaskById(HttpExchange exchange, Integer id) throws IOException {
        Subtask subtask = manager.getSubTask(id);
        sendText(exchange, 200, gson.toJson(subtask));
    }

    private void postCreateSubtask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {

            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtaskFromJson = gson.fromJson(body, Subtask.class);

            manager.createSubTask(subtaskFromJson);
            sendText(exchange, 201, "Подзадача успешно создана");
        }
    }

    private void postUpdateSubtask(HttpExchange exchange, Integer id) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {

            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtaskFromJson = gson.fromJson(body, Subtask.class);

            if (!subtaskFromJson.getId().equals(id)) {
                sendBadRequest(exchange, "id указан неверно или null");
                return;
            }

            manager.updateSubTask(subtaskFromJson);
            sendText(exchange, 201, "Задача успешно обновлена");
        }
    }

    private void deleteSubtask(HttpExchange exchange, Integer id) throws IOException {
        manager.deleteSubTask(id);
        sendText(exchange, 204, "");
    }
}