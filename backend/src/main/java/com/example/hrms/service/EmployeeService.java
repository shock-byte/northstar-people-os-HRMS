package com.example.hrms.service;

import com.example.hrms.dto.EmployeeDto;
import com.example.hrms.model.Department;
import com.example.hrms.model.Employee;
import com.example.hrms.repository.DepartmentRepository;
import com.example.hrms.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Comparator;

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
                                     Optional<String> query,
                                     Pageable pageable) {
        var filtered = employeeRepository.findAll().stream()
                .filter(employee -> departmentId
                        .map(id -> employee.getDepartment() != null && id.equals(employee.getDepartment().getId()))
                        .orElse(true))
                .filter(employee -> status.map(value -> employee.getStatus() == value).orElse(true))
                .filter(employee -> query
                        .map(term -> matchesSearch(employee, term))
                        .orElse(true))
                .sorted(Comparator.comparing(Employee::getLastName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Employee::getFirstName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        var pageContent = start >= filtered.size() ? java.util.List.<Employee>of() : filtered.subList(start, end);

        return new PageImpl<>(
                pageContent.stream().map(EmployeeDto::fromEntity).toList(),
                pageable,
                filtered.size()
        );
    }

    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
        return EmployeeDto.fromEntity(employee);
    }

    public EmployeeDto create(EmployeeDto request) {
        validateEmailUniqueness(request.getEmail(), null);
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
        validateEmailUniqueness(request.getEmail(), id);
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

    private void validateEmailUniqueness(String email, Long currentId) {
        employeeRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ValidationException("Employee email already exists: " + email);
                });
    }

    private boolean matchesSearch(Employee employee, String rawSearchTerm) {
        String term = rawSearchTerm == null ? "" : rawSearchTerm.trim().toLowerCase();
        if (term.isEmpty()) {
            return true;
        }

        return contains(employee.getFirstName(), term)
                || contains(employee.getLastName(), term)
                || contains(employee.getEmail(), term)
                || contains(employee.getJobTitle(), term)
                || contains(employee.getDepartment() != null ? employee.getDepartment().getName() : null, term);
    }

    private boolean contains(String value, String term) {
        return value != null && value.toLowerCase().contains(term);
    }
}
