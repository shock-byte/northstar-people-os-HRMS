package com.example.hrms.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record DashboardOverviewDto(
        Instant generatedAt,
        SummaryCards summary,
        AttendanceSnapshot attendance,
        LeaveSnapshot leave,
        PayrollSnapshot payroll,
        List<DepartmentSnapshot> departmentBreakdown,
        List<ActionItem> actionItems,
        List<Milestone> milestones
) {

    public record SummaryCards(
            long totalEmployees,
            long activeEmployees,
            long departments,
            long newHiresLast30Days
    ) {
    }

    public record AttendanceSnapshot(
            LocalDate workDate,
            long expectedEmployees,
            long loggedEmployees,
            long present,
            long late,
            long absent,
            double completionRate
    ) {
    }

    public record LeaveSnapshot(
            long pendingRequests,
            long approvedThisMonth,
            long employeesOnLeaveToday,
            double utilizationRate
    ) {
    }

    public record PayrollSnapshot(
            int year,
            int month,
            long processedEmployees,
            BigDecimal totalNetPay,
            BigDecimal totalAllowances,
            BigDecimal totalDeductions,
            BigDecimal averageNetPay,
            double coverageRate
    ) {
    }

    public record DepartmentSnapshot(
            Long departmentId,
            String name,
            String code,
            String managerName,
            long headcount,
            long activeHeadcount,
            BigDecimal monthlyPayroll,
            long pendingLeaveRequests
    ) {
    }

    public record ActionItem(
            String severity,
            String title,
            String detail,
            String route
    ) {
    }

    public record Milestone(
            String title,
            String detail,
            LocalDate date,
            String route
    ) {
    }
}
