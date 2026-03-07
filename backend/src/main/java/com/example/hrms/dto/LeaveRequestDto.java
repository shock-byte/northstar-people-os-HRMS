package com.example.hrms.dto;

import com.example.hrms.model.Employee;
import com.example.hrms.model.LeaveRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class LeaveRequestDto {

    private Long id;

    @NotNull
    private Long employeeId;

    private String employeeName;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private LeaveRequest.LeaveType type;

    private LeaveRequest.LeaveStatus status;

    private String approverName;

    private String comment;

    public static LeaveRequestDto fromEntity(LeaveRequest entity) {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setId(entity.getId());
        Employee employee = entity.getEmployee();
        if (employee != null) {
            dto.setEmployeeId(employee.getId());
            dto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
        }
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setType(entity.getType());
        dto.setStatus(entity.getStatus());
        dto.setApproverName(entity.getApproverName());
        dto.setComment(entity.getComment());
        return dto;
    }

    public void updateEntity(LeaveRequest entity, Employee employee) {
        entity.setEmployee(employee);
        entity.setStartDate(this.startDate);
        entity.setEndDate(this.endDate);
        entity.setType(this.type);
        if (this.status != null) {
            entity.setStatus(this.status);
        }
        entity.setApproverName(this.approverName);
        entity.setComment(this.comment);
    }
}

