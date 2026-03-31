package com.example.hrms.service;

import com.example.hrms.dto.DepartmentDto;
import com.example.hrms.model.Department;
import com.example.hrms.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ValidationException;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> findAll() {
        return departmentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Department::getName, String.CASE_INSENSITIVE_ORDER))
                .map(DepartmentDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentDto findById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found: " + id));
        return DepartmentDto.fromEntity(department);
    }

    public DepartmentDto create(DepartmentDto request) {
        validateUniqueness(request, null);
        Department department = new Department();
        request.updateEntity(department);
        Department saved = departmentRepository.save(department);
        return DepartmentDto.fromEntity(saved);
    }

    public DepartmentDto update(Long id, DepartmentDto request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found: " + id));
        validateUniqueness(request, id);
        request.updateEntity(department);
        Department saved = departmentRepository.save(department);
        return DepartmentDto.fromEntity(saved);
    }

    public void delete(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Department not found: " + id);
        }
        departmentRepository.deleteById(id);
    }

    private void validateUniqueness(DepartmentDto request, Long currentId) {
        departmentRepository.findByCode(request.getCode())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ValidationException("Department code already exists: " + request.getCode());
                });
        departmentRepository.findAll().stream()
                .filter(existing -> existing.getName().equalsIgnoreCase(request.getName()))
                .filter(existing -> !existing.getId().equals(currentId))
                .findFirst()
                .ifPresent(existing -> {
                    throw new ValidationException("Department name already exists: " + request.getName());
                });
    }
}
