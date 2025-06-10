package com.example;

import com.sun.net.httpserver.HttpServer;
import com.example.handlers.RootHandler;
import com.example.handlers.HealthHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Simple Java HTTP Server Application
 * Demonstrates containerization and ECR deployment
 */
public class DemoApplication {
    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_HOST = "0.0.0.0";

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(System.getenv().getOrDefault("PORT", DEFAULT_PORT));
            String host = System.getenv().getOrDefault("HOST", DEFAULT_HOST);

            // Create HTTP server
            HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);

            // Configure endpoints
            server.createContext("/", new RootHandler());
            server.createContext("/health", new HealthHandler());
            server.createContext("/api/info", new RootHandler());

            // Set thread pool executor
            server.setExecutor(Executors.newFixedThreadPool(10));

            // Start server
            server.start();

            System.out.println("🚀 Server started successfully!");
            System.out.println("📍 Listening on " + host + ":" + port);
            System.out.println("🔗 Available endpoints:");
            System.out.println("   - GET / (Welcome message)");
            System.out.println("   - GET /health (Health check)");
            System.out.println("   - GET /api/info (Application info)");

            // Graceful shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("🛑 Shutting down server...");
                server.stop(0);
                System.out.println("✅ Server stopped gracefully");
            }));

        } catch (IOException e) {
            System.err.println("❌ Failed to start server: " + e.getMessage());
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid port number: " + e.getMessage());
            System.exit(1);
        }
    }
}