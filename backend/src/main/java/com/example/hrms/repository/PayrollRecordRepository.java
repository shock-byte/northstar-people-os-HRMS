package com.example.hrms.repository;

import com.example.hrms.model.Employee;
import com.example.hrms.model.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, Long> {

    List<PayrollRecord> findByEmployee(Employee employee);

    Optional<PayrollRecord> findByEmployeeAndYearAndMonth(Employee employee, Integer year, Integer month);
}

