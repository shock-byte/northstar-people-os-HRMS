package com.example.hrms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ApiInfoController {

    @GetMapping
    public Map<String, Object> apiRoot() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "northstar-people-os-api");
        body.put("status", "OK");
        body.put("timestamp", Instant.now().toString());
        return body;
    }
}
