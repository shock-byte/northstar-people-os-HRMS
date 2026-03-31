package com.example.hrms.service;

import com.example.hrms.dto.PayrollRecordDto;
import com.example.hrms.model.Employee;
import com.example.hrms.model.PayrollRecord;
import com.example.hrms.repository.EmployeeRepository;
import com.example.hrms.repository.PayrollRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class PayrollService {

    private final PayrollRecordRepository payrollRecordRepository;
    private final EmployeeRepository employeeRepository;

    public PayrollService(PayrollRecordRepository payrollRecordRepository, EmployeeRepository employeeRepository) {
        this.payrollRecordRepository = payrollRecordRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<PayrollRecordDto> findAll() {
        return payrollRecordRepository.findAll().stream()
                .sorted(Comparator.comparing(PayrollRecord::getYear).reversed()
                        .thenComparing(Comparator.comparing(PayrollRecord::getMonth).reversed()))
                .map(PayrollRecordDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PayrollRecordDto findById(Long id) {
        PayrollRecord record = payrollRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payroll record not found: " + id));
        return PayrollRecordDto.fromEntity(record);
    }

    public PayrollRecordDto createOrUpdate(PayrollRecordDto request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + request.getEmployeeId()));
        PayrollRecord record = payrollRecordRepository
                .findByEmployeeAndYearAndMonth(employee, request.getYear(), request.getMonth())
                .orElseGet(PayrollRecord::new);
        request.updateEntity(record, employee);
        PayrollRecord saved = payrollRecordRepository.save(record);
        return PayrollRecordDto.fromEntity(saved);
    }

    public void delete(Long id) {
        if (!payrollRecordRepository.existsById(id)) {
            throw new EntityNotFoundException("Payroll record not found: " + id);
        }
        payrollRecordRepository.deleteById(id);
    }
}
