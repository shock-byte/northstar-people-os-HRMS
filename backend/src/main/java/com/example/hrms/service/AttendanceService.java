package com.example.hrms.service;

import com.example.hrms.dto.AttendanceRecordDto;
import com.example.hrms.model.AttendanceRecord;
import com.example.hrms.model.Employee;
import com.example.hrms.repository.AttendanceRecordRepository;
import com.example.hrms.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRecordRepository attendanceRecordRepository, EmployeeRepository employeeRepository) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordDto> findAll() {
        return attendanceRecordRepository.findAll()
                .stream()
                .map(AttendanceRecordDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AttendanceRecordDto findById(Long id) {
        AttendanceRecord record = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attendance record not found: " + id));
        return AttendanceRecordDto.fromEntity(record);
    }

    public AttendanceRecordDto createOrUpdateForDay(AttendanceRecordDto request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + request.getEmployeeId()));
        LocalDate date = request.getWorkDate();
        AttendanceRecord record = attendanceRecordRepository
                .findByEmployeeAndWorkDate(employee, date)
                .orElseGet(AttendanceRecord::new);
        request.updateEntity(record, employee);
        AttendanceRecord saved = attendanceRecordRepository.save(record);
        return AttendanceRecordDto.fromEntity(saved);
    }

    public void delete(Long id) {
        if (!attendanceRecordRepository.existsById(id)) {
            throw new EntityNotFoundException("Attendance record not found: " + id);
        }
        attendanceRecordRepository.deleteById(id);
    }
}

