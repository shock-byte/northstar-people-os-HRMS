package com.example.hrms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiInfoController {

    @GetMapping({"/", "/api/v1", "/api/v1/info"})
    public Map<String, Object> apiRoot() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "northstar-people-os-api");
        body.put("status", "OK");
        body.put("timestamp", Instant.now().toString());
        body.put("frontend", "http://127.0.0.1:4200");
        body.put("apiBase", "/api/v1");
        body.put("authentication", "HTTP Basic authentication is required for protected API routes");
        return body;
    }
}
