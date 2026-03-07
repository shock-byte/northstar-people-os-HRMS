package com.example.hrms.dto;

import com.example.hrms.model.Employee;
import com.example.hrms.model.PayrollRecord;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PayrollRecordDto {

    private Long id;

    @NotNull
    private Long employeeId;

    private String employeeName;

    @NotNull
    @Min(2000)
    @Max(9999)
    private Integer year;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer month;

    private BigDecimal baseSalary;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private BigDecimal netPay;

    public static PayrollRecordDto fromEntity(PayrollRecord entity) {
        PayrollRecordDto dto = new PayrollRecordDto();
        dto.setId(entity.getId());
        Employee employee = entity.getEmployee();
        if (employee != null) {
            dto.setEmployeeId(employee.getId());
            dto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
        }
        dto.setYear(entity.getYear());
        dto.setMonth(entity.getMonth());
        dto.setBaseSalary(entity.getBaseSalary());
        dto.setAllowances(entity.getAllowances());
        dto.setDeductions(entity.getDeductions());
        dto.setNetPay(entity.getNetPay());
        return dto;
    }

    public void updateEntity(PayrollRecord entity, Employee employee) {
        entity.setEmployee(employee);
        entity.setYear(this.year);
        entity.setMonth(this.month);
        entity.setBaseSalary(this.baseSalary);
        entity.setAllowances(this.allowances);
        entity.setDeductions(this.deductions);
        BigDecimal base = this.baseSalary != null ? this.baseSalary : BigDecimal.ZERO;
        BigDecimal allow = this.allowances != null ? this.allowances : BigDecimal.ZERO;
        BigDecimal ded = this.deductions != null ? this.deductions : BigDecimal.ZERO;
        entity.setNetPay(base.add(allow).subtract(ded));
    }
}

