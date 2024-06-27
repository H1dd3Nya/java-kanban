package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Endpoint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);

            if (statusCode == 200) {
                exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            }

            if (statusCode == 204) {
                exchange.sendResponseHeaders(statusCode, -1);
                return;
            }

            exchange.sendResponseHeaders(statusCode, resp.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp);
            }
        }
    }

    protected void sendBadRequest(HttpExchange exchange, String text) throws IOException {
        try (exchange) {
            System.out.println(text);
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, resp.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp);
            }
        }
    }

    protected void sendNotFound(HttpExchange exchange, Exception exception) throws IOException {
        try (exchange) {
            exception.printStackTrace();
            byte[] resp = (exception.getMessage() + "\nid указан неверно или null").getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, resp.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp);
            }
        }
    }

    protected void sendHasInteractions(HttpExchange exchange, Exception exception) throws IOException {
        try (exchange) {
            exception.printStackTrace();
            byte[] resp = exception.getMessage().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(406, resp.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp);
            }
        }
    }

    protected void sendInternalError(HttpExchange exchange, Exception exception) throws IOException {
        try (exchange) {
            exception.printStackTrace();
            byte[] resp = exception.getMessage().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, resp.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp);
            }
        }
    }

    protected Endpoint getTasksEndpoint(String requestPath, String requestMethod) {
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

    protected Endpoint getSubtasksEndpoint(String requestPath, String requestMethod) {
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

    protected Endpoint getEpicsEndpoint(String requestPath, String requestMethod) {
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
}