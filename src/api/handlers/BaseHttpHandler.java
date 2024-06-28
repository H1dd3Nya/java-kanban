package api.handlers;

import com.sun.net.httpserver.HttpExchange;

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
}