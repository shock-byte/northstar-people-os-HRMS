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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final BigDecimal MONTHLY_WORKING_DAYS = new BigDecimal("22");

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final PayrollRecordRepository payrollRecordRepository;

    public DashboardService(EmployeeRepository employeeRepository,
                            DepartmentRepository departmentRepository,
                            LeaveRequestRepository leaveRequestRepository,
                            AttendanceRecordRepository attendanceRecordRepository,
                            PayrollRecordRepository payrollRecordRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.payrollRecordRepository = payrollRecordRepository;
    }

    public DashboardOverviewDto getOverview() {
        List<Employee> employees = employeeRepository.findAll();
        List<Department> departments = departmentRepository.findAll().stream()
                .sorted(Comparator.comparing(Department::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
        List<AttendanceRecord> attendanceRecords = attendanceRecordRepository.findAll();
        List<PayrollRecord> payrollRecords = payrollRecordRepository.findAll();

        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();

        long activeEmployees = employees.stream()
                .filter(employee -> employee.getStatus() == Employee.EmploymentStatus.ACTIVE)
                .count();
        long newHiresLast30Days = employees.stream()
                .filter(employee -> employee.getHireDate() != null)
                .filter(employee -> !employee.getHireDate().isBefore(today.minusDays(30)))
                .count();

        List<AttendanceRecord> todayAttendance = attendanceRecords.stream()
                .filter(record -> today.equals(record.getWorkDate()))
                .toList();
        long loggedEmployees = todayAttendance.size();
        long present = todayAttendance.stream()
                .filter(record -> record.getStatus() == AttendanceRecord.AttendanceStatus.PRESENT
                        || record.getStatus() == AttendanceRecord.AttendanceStatus.HALF_DAY)
                .count();
        long late = todayAttendance.stream()
                .filter(record -> record.getStatus() == AttendanceRecord.AttendanceStatus.LATE)
                .count();
        long recordedAbsences = todayAttendance.stream()
                .filter(record -> record.getStatus() == AttendanceRecord.AttendanceStatus.ABSENT)
                .count();
        long absent = Math.max(recordedAbsences, activeEmployees - loggedEmployees);
        double attendanceCompletionRate = percentage(loggedEmployees, activeEmployees);

        List<LeaveRequest> currentMonthApprovedLeaves = leaveRequests.stream()
                .filter(request -> request.getStatus() == LeaveRequest.LeaveStatus.APPROVED)
                .filter(request -> YearMonth.from(request.getStartDate()).equals(currentMonth)
                        || YearMonth.from(request.getEndDate()).equals(currentMonth))
                .toList();
        long pendingRequests = leaveRequests.stream()
                .filter(request -> request.getStatus() == LeaveRequest.LeaveStatus.PENDING)
                .count();
        long employeesOnLeaveToday = leaveRequests.stream()
                .filter(request -> request.getStatus() == LeaveRequest.LeaveStatus.APPROVED)
                .filter(request -> !request.getStartDate().isAfter(today) && !request.getEndDate().isBefore(today))
                .map(request -> request.getEmployee().getId())
                .distinct()
                .count();
        long approvedLeaveDaysThisMonth = currentMonthApprovedLeaves.stream()
                .mapToLong(request -> calculateLeaveDaysInMonth(request, currentMonth))
                .sum();
        double leaveUtilizationRate = activeEmployees == 0
                ? 0
                : BigDecimal.valueOf(approvedLeaveDaysThisMonth)
                .divide(BigDecimal.valueOf(activeEmployees).multiply(MONTHLY_WORKING_DAYS), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        List<PayrollRecord> currentMonthPayroll = payrollRecords.stream()
                .filter(record -> record.getYear() == currentMonth.getYear() && record.getMonth() == currentMonth.getMonthValue())
                .toList();
        BigDecimal totalNetPay = sumPayrollField(currentMonthPayroll, PayrollRecord::getNetPay);
        BigDecimal totalAllowances = sumPayrollField(currentMonthPayroll, PayrollRecord::getAllowances);
        BigDecimal totalDeductions = sumPayrollField(currentMonthPayroll, PayrollRecord::getDeductions);
        BigDecimal averageNetPay = currentMonthPayroll.isEmpty()
                ? BigDecimal.ZERO
                : totalNetPay.divide(BigDecimal.valueOf(currentMonthPayroll.size()), 2, RoundingMode.HALF_UP);
        double payrollCoverageRate = percentage(currentMonthPayroll.size(), activeEmployees);

        List<DashboardOverviewDto.DepartmentSnapshot> departmentSnapshots = departments.stream()
                .map(department -> toDepartmentSnapshot(department, employees, leaveRequests, currentMonthPayroll))
                .toList();

        List<DashboardOverviewDto.ActionItem> actionItems = buildActionItems(
                leaveRequests,
                todayAttendance,
                employees,
                activeEmployees,
                currentMonthPayroll,
                currentMonth
        );

        List<DashboardOverviewDto.Milestone> milestones = buildMilestones(employees, today);

        return new DashboardOverviewDto(
                Instant.now(),
                new DashboardOverviewDto.SummaryCards(
                        employees.size(),
                        activeEmployees,
                        departments.size(),
                        newHiresLast30Days
                ),
                new DashboardOverviewDto.AttendanceSnapshot(
                        today,
                        activeEmployees,
                        loggedEmployees,
                        present,
                        late,
                        absent,
                        attendanceCompletionRate
                ),
                new DashboardOverviewDto.LeaveSnapshot(
                        pendingRequests,
                        currentMonthApprovedLeaves.size(),
                        employeesOnLeaveToday,
                        leaveUtilizationRate
                ),
                new DashboardOverviewDto.PayrollSnapshot(
                        currentMonth.getYear(),
                        currentMonth.getMonthValue(),
                        currentMonthPayroll.size(),
                        totalNetPay,
                        totalAllowances,
                        totalDeductions,
                        averageNetPay,
                        payrollCoverageRate
                ),
                departmentSnapshots,
                actionItems,
                milestones
        );
    }

    private DashboardOverviewDto.DepartmentSnapshot toDepartmentSnapshot(Department department,
                                                                        List<Employee> employees,
                                                                        List<LeaveRequest> leaveRequests,
                                                                        List<PayrollRecord> currentMonthPayroll) {
        List<Employee> departmentEmployees = employees.stream()
                .filter(employee -> employee.getDepartment() != null)
                .filter(employee -> Objects.equals(employee.getDepartment().getId(), department.getId()))
                .toList();
        Set<Long> employeeIds = departmentEmployees.stream()
                .map(Employee::getId)
                .collect(Collectors.toSet());
        long activeHeadcount = departmentEmployees.stream()
                .filter(employee -> employee.getStatus() == Employee.EmploymentStatus.ACTIVE)
                .count();
        BigDecimal monthlyPayroll = currentMonthPayroll.stream()
                .filter(record -> employeeIds.contains(record.getEmployee().getId()))
                .map(record -> defaultBigDecimal(record.getNetPay()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long pendingLeaveRequests = leaveRequests.stream()
                .filter(request -> request.getStatus() == LeaveRequest.LeaveStatus.PENDING)
                .filter(request -> employeeIds.contains(request.getEmployee().getId()))
                .count();

        return new DashboardOverviewDto.DepartmentSnapshot(
                department.getId(),
                department.getName(),
                department.getCode(),
                department.getManagerName(),
                departmentEmployees.size(),
                activeHeadcount,
                monthlyPayroll,
                pendingLeaveRequests
        );
    }

    private List<DashboardOverviewDto.ActionItem> buildActionItems(List<LeaveRequest> leaveRequests,
                                                                   List<AttendanceRecord> todayAttendance,
                                                                   List<Employee> employees,
                                                                   long activeEmployees,
                                                                   List<PayrollRecord> currentMonthPayroll,
                                                                   YearMonth currentMonth) {
        List<DashboardOverviewDto.ActionItem> items = new ArrayList<>();

        leaveRequests.stream()
                .filter(request -> request.getStatus() == LeaveRequest.LeaveStatus.PENDING)
                .sorted(Comparator.comparing(LeaveRequest::getStartDate))
                .limit(3)
                .forEach(request -> items.add(new DashboardOverviewDto.ActionItem(
                        "medium",
                        request.getEmployee().getFirstName() + " " + request.getEmployee().getLastName() + " needs leave approval",
                        request.getType() + " leave from " + request.getStartDate() + " to " + request.getEndDate(),
                        "/leave"
                )));

        todayAttendance.stream()
                .filter(record -> record.getStatus() == AttendanceRecord.AttendanceStatus.ABSENT
                        || record.getStatus() == AttendanceRecord.AttendanceStatus.LATE)
                .sorted(Comparator.comparing(AttendanceRecord::getStatus))
                .limit(2)
                .forEach(record -> items.add(new DashboardOverviewDto.ActionItem(
                        "high",
                        record.getEmployee().getFirstName() + " " + record.getEmployee().getLastName() + " is marked " + record.getStatus().name().toLowerCase(),
                        "Check attendance exception for " + record.getWorkDate(),
                        "/attendance"
                )));

        if (currentMonthPayroll.size() < activeEmployees) {
            items.add(new DashboardOverviewDto.ActionItem(
                    "medium",
                    "Payroll coverage is incomplete",
                    currentMonthPayroll.size() + " of " + activeEmployees + " active employees have payroll for "
                            + currentMonth.getMonth() + " " + currentMonth.getYear(),
                    "/payroll"
            ));
        }

        employees.stream()
                .filter(employee -> employee.getDepartment() == null)
                .limit(2)
                .forEach(employee -> items.add(new DashboardOverviewDto.ActionItem(
                        "low",
                        employee.getFirstName() + " " + employee.getLastName() + " is missing a department assignment",
                        "Assign the employee to the right business unit for reporting and approvals.",
                        "/employees"
                )));

        return items.stream().limit(6).toList();
    }

    private List<DashboardOverviewDto.Milestone> buildMilestones(List<Employee> employees, LocalDate today) {
        return employees.stream()
                .filter(employee -> employee.getHireDate() != null)
                .map(employee -> {
                    LocalDate nextAnniversary = employee.getHireDate().withYear(today.getYear());
                    if (nextAnniversary.isBefore(today)) {
                        nextAnniversary = nextAnniversary.plusYears(1);
                    }
                    long years = ChronoUnit.YEARS.between(employee.getHireDate(), nextAnniversary);
                    return new DashboardOverviewDto.Milestone(
                            employee.getFirstName() + " " + employee.getLastName(),
                            years + " year work anniversary in " + safeDepartmentName(employee),
                            nextAnniversary,
                            "/employees"
                    );
                })
                .filter(milestone -> !milestone.date().isAfter(today.plusDays(60)))
                .sorted(Comparator.comparing(DashboardOverviewDto.Milestone::date))
                .limit(6)
                .toList();
    }

    private long calculateLeaveDaysInMonth(LeaveRequest request, YearMonth month) {
        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();
        LocalDate start = request.getStartDate().isBefore(monthStart) ? monthStart : request.getStartDate();
        LocalDate end = request.getEndDate().isAfter(monthEnd) ? monthEnd : request.getEndDate();
        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    private BigDecimal sumPayrollField(List<PayrollRecord> records,
                                       java.util.function.Function<PayrollRecord, BigDecimal> extractor) {
        return records.stream()
                .map(extractor)
                .map(this::defaultBigDecimal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal defaultBigDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private double percentage(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0;
        }
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private String safeDepartmentName(Employee employee) {
        return employee.getDepartment() != null ? employee.getDepartment().getName() : "Unassigned";
    }
}
