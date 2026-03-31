package com.example.hrms.service;

import com.example.hrms.dto.AttendanceRecordDto;
import com.example.hrms.model.AttendanceRecord;
import com.example.hrms.model.Employee;
import com.example.hrms.repository.AttendanceRecordRepository;
import com.example.hrms.repository.EmployeeRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AttendanceServiceTest {

    @Test
    void createOrUpdateForDay_rejectsCheckoutBeforeCheckin() {
        AttendanceRecordRepository attendanceRecordRepository = mock(AttendanceRecordRepository.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);

        Employee employee = new Employee();
        employee.setId(1L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRecordRepository.findByEmployeeAndWorkDate(employee, LocalDate.now()))
                .thenReturn(Optional.of(new AttendanceRecord()));

        AttendanceService service = new AttendanceService(attendanceRecordRepository, employeeRepository);

        AttendanceRecordDto dto = new AttendanceRecordDto();
        dto.setEmployeeId(1L);
        dto.setWorkDate(LocalDate.now());
        dto.setStatus(AttendanceRecord.AttendanceStatus.PRESENT);
        dto.setCheckInTime(LocalTime.of(18, 0));
        dto.setCheckOutTime(LocalTime.of(9, 0));

        assertThatThrownBy(() -> service.createOrUpdateForDay(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Check-out time");
    }
}
