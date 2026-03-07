package com.example.hrms.service;

import com.example.hrms.dto.DepartmentDto;
import com.example.hrms.model.Department;
import com.example.hrms.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Department department = new Department();
        request.updateEntity(department);
        Department saved = departmentRepository.save(department);
        return DepartmentDto.fromEntity(saved);
    }

    public DepartmentDto update(Long id, DepartmentDto request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found: " + id));
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
}

