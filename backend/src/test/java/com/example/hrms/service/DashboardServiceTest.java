package com.example.hrms.service;

import com.example.hrms.dto.DashboardOverviewDto;
import com.example.hrms.model.AttendanceRecord;
import com.example.hrms.model.Department;
import com.example.hrms.model.Employee;
import com.example.hrms.model.LeaveRequest;
import com.example.hrms.model.PayrollRecord;
import com.example.hrms.repository.AttendanceRecordRepository;
import com.example.hrms.repository.DepartmentRepository;
import com.example.hrms.repository.EmployeeRepository;
import com.example.hrms.repository.LeaveRequestRepository;
import com.example.hrms.repository.PayrollRecordRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Test
    void getOverview_aggregatesOperationalMetrics() {
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
        LeaveRequestRepository leaveRequestRepository = mock(LeaveRequestRepository.class);
        AttendanceRecordRepository attendanceRecordRepository = mock(AttendanceRecordRepository.class);
        PayrollRecordRepository payrollRecordRepository = mock(PayrollRecordRepository.class);

        Department department = new Department();
        department.setId(1L);
        department.setName("People");
        department.setCode("PEOPLE");
        department.setManagerName("Nina Verma");

        Employee activeEmployee = new Employee();
        activeEmployee.setId(1L);
        activeEmployee.setFirstName("Aisha");
        activeEmployee.setLastName("Khan");
        activeEmployee.setDepartment(department);
        activeEmployee.setHireDate(LocalDate.now().minusDays(10));
        activeEmployee.setStatus(Employee.EmploymentStatus.ACTIVE);

        Employee secondActiveEmployee = new Employee();
        secondActiveEmployee.setId(2L);
        secondActiveEmployee.setFirstName("Eva");
        secondActiveEmployee.setLastName("Nguyen");
        secondActiveEmployee.setDepartment(department);
        secondActiveEmployee.setHireDate(LocalDate.now().minusYears(2));
        secondActiveEmployee.setStatus(Employee.EmploymentStatus.ACTIVE);

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(activeEmployee);
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.PENDING);
        leaveRequest.setType(LeaveRequest.LeaveType.ANNUAL);
        leaveRequest.setStartDate(LocalDate.now().plusDays(2));
        leaveRequest.setEndDate(LocalDate.now().plusDays(4));

        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setEmployee(activeEmployee);
        attendanceRecord.setWorkDate(LocalDate.now());
        attendanceRecord.setStatus(AttendanceRecord.AttendanceStatus.PRESENT);
        attendanceRecord.setCheckInTime(LocalTime.of(9, 0));
        attendanceRecord.setCheckOutTime(LocalTime.of(18, 0));

        PayrollRecord payrollRecord = new PayrollRecord();
        payrollRecord.setEmployee(activeEmployee);
        payrollRecord.setYear(YearMonth.now().getYear());
        payrollRecord.setMonth(YearMonth.now().getMonthValue());
        payrollRecord.setAllowances(new BigDecimal("100.00"));
        payrollRecord.setDeductions(new BigDecimal("50.00"));
        payrollRecord.setNetPay(new BigDecimal("1050.00"));

        when(employeeRepository.findAll()).thenReturn(List.of(activeEmployee, secondActiveEmployee));
        when(departmentRepository.findAll()).thenReturn(List.of(department));
        when(leaveRequestRepository.findAll()).thenReturn(List.of(leaveRequest));
        when(attendanceRecordRepository.findAll()).thenReturn(List.of(attendanceRecord));
        when(payrollRecordRepository.findAll()).thenReturn(List.of(payrollRecord));

        DashboardService service = new DashboardService(
                employeeRepository,
                departmentRepository,
                leaveRequestRepository,
                attendanceRecordRepository,
                payrollRecordRepository
        );

        DashboardOverviewDto overview = service.getOverview();

        assertThat(overview.summary().totalEmployees()).isEqualTo(2);
        assertThat(overview.summary().activeEmployees()).isEqualTo(2);
        assertThat(overview.leave().pendingRequests()).isEqualTo(1);
        assertThat(overview.payroll().processedEmployees()).isEqualTo(1);
        assertThat(overview.departmentBreakdown()).hasSize(1);
        assertThat(overview.actionItems()).isNotEmpty();
    }
}
