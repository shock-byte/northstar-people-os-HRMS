package com.example.hrms.service;

import com.example.hrms.dto.PayrollRecordDto;
import com.example.hrms.model.Employee;
import com.example.hrms.model.PayrollRecord;
import com.example.hrms.repository.EmployeeRepository;
import com.example.hrms.repository.PayrollRecordRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PayrollServiceTest {

    @Test
    void createOrUpdate_calculatesNetPay() {
        PayrollRecordRepository payrollRepo = mock(PayrollRecordRepository.class);
        EmployeeRepository employeeRepo = mock(EmployeeRepository.class);

        Employee employee = new Employee();
        employee.setId(1L);
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(employee));
        when(payrollRepo.findByEmployeeAndYearAndMonth(any(), any(), any())).thenReturn(Optional.empty());
        when(payrollRepo.save(any(PayrollRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayrollService service = new PayrollService(payrollRepo, employeeRepo);

        PayrollRecordDto dto = new PayrollRecordDto();
        dto.setEmployeeId(1L);
        dto.setYear(2026);
        dto.setMonth(3);
        dto.setBaseSalary(new BigDecimal("1000.00"));
        dto.setAllowances(new BigDecimal("200.00"));
        dto.setDeductions(new BigDecimal("150.00"));

        PayrollRecordDto result = service.createOrUpdate(dto);

        assertThat(result.getNetPay()).isEqualByComparingTo(new BigDecimal("1050.00"));
    }
}

