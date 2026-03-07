package com.example.hrms.controller;

import com.example.hrms.dto.PayrollRecordDto;
import com.example.hrms.service.PayrollService;
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
@RequestMapping("/api/v1/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping
    public List<PayrollRecordDto> getAll() {
        return payrollService.findAll();
    }

    @GetMapping("/{id}")
    public PayrollRecordDto getById(@PathVariable Long id) {
        return payrollService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PayrollRecordDto> createOrUpdate(@Valid @RequestBody PayrollRecordDto request) {
        PayrollRecordDto dto = payrollService.createOrUpdate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        payrollService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

