package com.example.hrms.repository;

import com.example.hrms.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Employee> findByDepartment_Id(Long departmentId, Pageable pageable);

    Page<Employee> findByStatus(Employee.EmploymentStatus status, Pageable pageable);
}

