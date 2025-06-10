package com.example.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DemoController {

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello from Java ECR Demo!");
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/api/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> info = new HashMap<>();
        info.put("application", "java-ecr-demo");
        info.put("version", "1.0.0");
        info.put("java.version", System.getProperty("java.version"));
        info.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(info);
    }
}