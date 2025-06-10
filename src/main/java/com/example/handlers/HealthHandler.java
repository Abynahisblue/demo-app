package com.example.handlers;


import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class HealthHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            handleHealthCheck(exchange);
        } else {
            handleMethodNotAllowed(exchange);
        }
    }

    private void handleHealthCheck(HttpExchange exchange) throws IOException {
        try {
            // Perform basic health checks
            boolean memoryOk = checkMemoryHealth();
            boolean systemOk = checkSystemHealth();

            String status = (memoryOk && systemOk) ? "healthy" : "unhealthy";
            int statusCode = (memoryOk && systemOk) ? 200 : 503;

            String response = String.format(
                    "{\n" +
                            "  \"status\": \"%s\",\n" +
                            "  \"timestamp\": \"%s\",\n" +
                            "  \"checks\": {\n" +
                            "    \"memory\": \"%s\",\n" +
                            "    \"system\": \"%s\"\n" +
                            "  },\n" +
                            "  \"uptime_ms\": %d\n" +
                            "}",
                    status,
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    memoryOk ? "ok" : "warning",
                    systemOk ? "ok" : "error",
                    getUptimeMillis()
            );

            sendJsonResponse(exchange, statusCode, response);

        } catch (Exception e) {
            String errorResponse = String.format(
                    "{\n  \"status\": \"error\",\n  \"message\": \"%s\",\n  \"timestamp\": \"%s\"\n}",
                    e.getMessage(),
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            );

            sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    private boolean checkMemoryHealth() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;

        // Consider unhealthy if using more than 90% of max memory
        return memoryUsagePercent < 90.0;
    }

    private boolean checkSystemHealth() {
        // Basic system checks
        try {
            // Check if we can create a temporary object
            new Object();

            // Check if current time is accessible
            Instant.now();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private long getUptimeMillis() {
        // Simple uptime calculation based on JVM start time
        return System.currentTimeMillis() -
                java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    private void handleMethodNotAllowed(HttpExchange exchange) throws IOException {
        String response = "{\n  \"error\": \"Method not allowed\",\n  \"allowed_methods\": [\"GET\"]\n}";
        sendJsonResponse(exchange, 405, response);
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.sendResponseHeaders(statusCode, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
