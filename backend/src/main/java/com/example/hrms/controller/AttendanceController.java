package com.example.hrms.controller;

import com.example.hrms.dto.AttendanceRecordDto;
import com.example.hrms.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public List<AttendanceRecordDto> getAll() {
        return attendanceService.findAll();
    }

    @GetMapping("/{id}")
    public AttendanceRecordDto getById(@PathVariable Long id) {
        return attendanceService.findById(id);
    }

    @PostMapping
    public ResponseEntity<AttendanceRecordDto> createOrUpdate(@Valid @RequestBody AttendanceRecordDto request) {
        AttendanceRecordDto dto = attendanceService.createOrUpdateForDay(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attendanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

