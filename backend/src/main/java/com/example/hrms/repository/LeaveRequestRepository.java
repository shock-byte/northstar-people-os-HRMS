package com.example.hrms.repository;

import com.example.hrms.model.LeaveRequest;
import com.example.hrms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee(Employee employee);

    @Query("""
            select lr from LeaveRequest lr
            where lr.employee = :employee
              and lr.status in (com.example.hrms.model.LeaveRequest$LeaveStatus.PENDING,
                                com.example.hrms.model.LeaveRequest$LeaveStatus.APPROVED)
              and lr.startDate <= :endDate
              and lr.endDate >= :startDate
            """)
    List<LeaveRequest> findOverlappingApprovedOrPending(
            @Param("employee") Employee employee,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

