package com.example.hrms.dto;

import com.example.hrms.model.AttendanceRecord;
import com.example.hrms.model.Employee;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceRecordDto {

    private Long id;

    @NotNull
    private Long employeeId;

    private String employeeName;

    @NotNull
    private LocalDate workDate;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    @NotNull
    private AttendanceRecord.AttendanceStatus status;

    public static AttendanceRecordDto fromEntity(AttendanceRecord entity) {
        AttendanceRecordDto dto = new AttendanceRecordDto();
        dto.setId(entity.getId());
        Employee employee = entity.getEmployee();
        if (employee != null) {
            dto.setEmployeeId(employee.getId());
            dto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
        }
        dto.setWorkDate(entity.getWorkDate());
        dto.setCheckInTime(entity.getCheckInTime());
        dto.setCheckOutTime(entity.getCheckOutTime());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    public void updateEntity(AttendanceRecord entity, Employee employee) {
        entity.setEmployee(employee);
        entity.setWorkDate(this.workDate);
        entity.setCheckInTime(this.checkInTime);
        entity.setCheckOutTime(this.checkOutTime);
        entity.setStatus(this.status);
    }
}

