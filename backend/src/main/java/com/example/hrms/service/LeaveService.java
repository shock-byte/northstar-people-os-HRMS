package com.example.hrms.service;

import com.example.hrms.dto.LeaveRequestDto;
import com.example.hrms.model.Employee;
import com.example.hrms.model.LeaveRequest;
import com.example.hrms.repository.EmployeeRepository;
import com.example.hrms.repository.LeaveRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Comparator;

@Service
@Transactional
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveService(LeaveRequestRepository leaveRequestRepository, EmployeeRepository employeeRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<LeaveRequestDto> findAll() {
        return leaveRequestRepository.findAll().stream()
                .sorted(Comparator.comparing(LeaveRequest::getStartDate).reversed())
                .map(LeaveRequestDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public LeaveRequestDto findById(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found: " + id));
        return LeaveRequestDto.fromEntity(leaveRequest);
    }

    public LeaveRequestDto create(LeaveRequestDto request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + request.getEmployeeId()));
        validateNoOverlap(employee, request);
        LeaveRequest leaveRequest = new LeaveRequest();
        request.updateEntity(leaveRequest, employee);
        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return LeaveRequestDto.fromEntity(saved);
    }

    public LeaveRequestDto update(Long id, LeaveRequestDto request) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found: " + id));
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + request.getEmployeeId()));
        validateNoOverlap(employee, request, id);
        request.updateEntity(leaveRequest, employee);
        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return LeaveRequestDto.fromEntity(saved);
    }

    public LeaveRequestDto changeStatus(Long id, LeaveRequest.LeaveStatus status, String approverName) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found: " + id));
        if (leaveRequest.getStatus() == LeaveRequest.LeaveStatus.CANCELLED && status != LeaveRequest.LeaveStatus.CANCELLED) {
            throw new ValidationException("Cancelled leave requests cannot be reopened");
        }
        leaveRequest.setStatus(status);
        leaveRequest.setApproverName(approverName);
        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return LeaveRequestDto.fromEntity(saved);
    }

    public void delete(Long id) {
        if (!leaveRequestRepository.existsById(id)) {
            throw new EntityNotFoundException("Leave request not found: " + id);
        }
        leaveRequestRepository.deleteById(id);
    }

    private void validateNoOverlap(Employee employee, LeaveRequestDto request) {
        validateNoOverlap(employee, request, null);
    }

    private void validateNoOverlap(Employee employee, LeaveRequestDto request, Long currentId) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ValidationException("End date cannot be before start date");
        }
        var overlapping = leaveRequestRepository.findOverlappingApprovedOrPending(
                employee, request.getStartDate(), request.getEndDate());
        boolean hasConflict = overlapping.stream()
                .anyMatch(lr -> currentId == null || !lr.getId().equals(currentId));
        if (hasConflict) {
            throw new ValidationException("Employee already has approved or pending leave in this period");
        }
    }
}
