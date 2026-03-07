package com.example.hrms.repository;

import com.example.hrms.model.AttendanceRecord;
import com.example.hrms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByEmployee(Employee employee);

    Optional<AttendanceRecord> findByEmployeeAndWorkDate(Employee employee, LocalDate workDate);
}

