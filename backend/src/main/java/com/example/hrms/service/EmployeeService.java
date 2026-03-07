package com.example.hrms.service;

import com.example.hrms.dto.EmployeeDto;
import com.example.hrms.model.Department;
import com.example.hrms.model.Employee;
import com.example.hrms.repository.DepartmentRepository;
import com.example.hrms.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDto> findAll(Optional<Long> departmentId,
                                     Optional<Employee.EmploymentStatus> status,
                                     Pageable pageable) {
        Page<Employee> page;
        if (departmentId.isPresent()) {
            page = employeeRepository.findByDepartment_Id(departmentId.get(), pageable);
        } else if (status.isPresent()) {
            page = employeeRepository.findByStatus(status.get(), pageable);
        } else {
            page = employeeRepository.findAll(pageable);
        }
        return page.map(EmployeeDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
        return EmployeeDto.fromEntity(employee);
    }

    public EmployeeDto create(EmployeeDto request) {
        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found: " + request.getDepartmentId()));
        }
        Employee employee = new Employee();
        request.updateEntity(employee, department);
        Employee saved = employeeRepository.save(employee);
        return EmployeeDto.fromEntity(saved);
    }

    public EmployeeDto update(Long id, EmployeeDto request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found: " + request.getDepartmentId()));
        } else {
            employee.setDepartment(null);
        }
        request.updateEntity(employee, department);
        Employee saved = employeeRepository.save(employee);
        return EmployeeDto.fromEntity(saved);
    }

    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found: " + id);
        }
        employeeRepository.deleteById(id);
    }
}

