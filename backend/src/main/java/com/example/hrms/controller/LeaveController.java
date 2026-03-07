package com.example.hrms.controller;

import com.example.hrms.dto.LeaveRequestDto;
import com.example.hrms.model.LeaveRequest;
import com.example.hrms.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-requests")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping
    public List<LeaveRequestDto> getAll() {
        return leaveService.findAll();
    }

    @GetMapping("/{id}")
    public LeaveRequestDto getById(@PathVariable Long id) {
        return leaveService.findById(id);
    }

    @PostMapping
    public ResponseEntity<LeaveRequestDto> create(@Valid @RequestBody LeaveRequestDto request) {
        LeaveRequestDto created = leaveService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public LeaveRequestDto update(@PathVariable Long id, @Valid @RequestBody LeaveRequestDto request) {
        return leaveService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public LeaveRequestDto changeStatus(
            @PathVariable Long id,
            @RequestParam LeaveRequest.LeaveStatus status,
            @RequestParam(required = false) String approverName
    ) {
        return leaveService.changeStatus(id, status, approverName);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

