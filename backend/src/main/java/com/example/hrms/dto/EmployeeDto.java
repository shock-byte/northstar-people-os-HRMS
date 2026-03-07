package com.example.hrms.dto;

import com.example.hrms.model.Department;
import com.example.hrms.model.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeDto {

    private Long id;

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @Email
    @NotBlank
    @Size(max = 150)
    private String email;

    @Size(max = 30)
    private String phone;

    @NotNull
    private LocalDate hireDate;

    @Size(max = 100)
    private String jobTitle;

    @NotNull
    private Employee.EmploymentStatus status = Employee.EmploymentStatus.ACTIVE;

    private Long departmentId;

    private String departmentName;

    private BigDecimal monthlySalary;

    public static EmployeeDto fromEntity(Employee entity) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setHireDate(entity.getHireDate());
        dto.setJobTitle(entity.getJobTitle());
        dto.setStatus(entity.getStatus());
        Department department = entity.getDepartment();
        if (department != null) {
            dto.setDepartmentId(department.getId());
            dto.setDepartmentName(department.getName());
        }
        dto.setMonthlySalary(entity.getMonthlySalary());
        return dto;
    }

    public void updateEntity(Employee entity, Department department) {
        entity.setFirstName(this.firstName);
        entity.setLastName(this.lastName);
        entity.setEmail(this.email);
        entity.setPhone(this.phone);
        entity.setHireDate(this.hireDate);
        entity.setJobTitle(this.jobTitle);
        entity.setStatus(this.status);
        entity.setDepartment(department);
        entity.setMonthlySalary(this.monthlySalary);
    }
}

