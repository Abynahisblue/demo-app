package com.example.handlers;


import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class RootHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method)) {
            handleGetRequest(exchange, path);
        } else {
            handleMethodNotAllowed(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        String response;

        if ("/api/info".equals(path)) {
            response = buildInfoResponse();
        } else {
            response = buildWelcomeResponse();
        }

        sendJsonResponse(exchange, 200, response);
    }

    private String buildWelcomeResponse() {
        return String.format(
                "{\n" +
                        "  \"message\": \"🎉 Hello from Java Docker Container!\",\n" +
                        "  \"application\": \"Java ECR Demo App\",\n" +
                        "  \"version\": \"1.0.0\",\n" +
                        "  \"timestamp\": \"%s\",\n" +
                        "  \"environment\": {\n" +
                        "    \"java_version\": \"%s\",\n" +
                        "    \"os\": \"%s\",\n" +
                        "    \"container\": true\n" +
                        "  }\n" +
                        "}",
                DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                System.getProperty("java.version"),
                System.getProperty("os.name")
        );
    }

    private String buildInfoResponse() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        return String.format(
                "{\n" +
                        "  \"application\": \"Java ECR Demo App\",\n" +
                        "  \"version\": \"1.0.0\",\n" +
                        "  \"build_time\": \"%s\",\n" +
                        "  \"runtime_info\": {\n" +
                        "    \"java_version\": \"%s\",\n" +
                        "    \"java_vendor\": \"%s\",\n" +
                        "    \"os_name\": \"%s\",\n" +
                        "    \"os_arch\": \"%s\",\n" +
                        "    \"available_processors\": %d\n" +
                        "  },\n" +
                        "  \"memory_info\": {\n" +
                        "    \"max_memory_mb\": %.2f,\n" +
                        "    \"total_memory_mb\": %.2f,\n" +
                        "    \"free_memory_mb\": %.2f,\n" +
                        "    \"used_memory_mb\": %.2f\n" +
                        "  }\n" +
                        "}",
                DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                System.getProperty("java.version"),
                System.getProperty("java.vendor"),
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                Runtime.getRuntime().availableProcessors(),
                maxMemory / 1024.0 / 1024.0,
                totalMemory / 1024.0 / 1024.0,
                freeMemory / 1024.0 / 1024.0,
                (totalMemory - freeMemory) / 1024.0 / 1024.0
        );
    }

    private void handleMethodNotAllowed(HttpExchange exchange) throws IOException {
        String response = "{\n  \"error\": \"Method not allowed\",\n  \"allowed_methods\": [\"GET\"]\n}";
        sendJsonResponse(exchange, 405, response);
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}