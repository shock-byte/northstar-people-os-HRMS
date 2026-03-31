export interface DashboardOverview {
  generatedAt: string;
  summary: {
    totalEmployees: number;
    activeEmployees: number;
    departments: number;
    newHiresLast30Days: number;
  };
  attendance: {
    workDate: string;
    expectedEmployees: number;
    loggedEmployees: number;
    present: number;
    late: number;
    absent: number;
    completionRate: number;
  };
  leave: {
    pendingRequests: number;
    approvedThisMonth: number;
    employeesOnLeaveToday: number;
    utilizationRate: number;
  };
  payroll: {
    year: number;
    month: number;
    processedEmployees: number;
    totalNetPay: number;
    totalAllowances: number;
    totalDeductions: number;
    averageNetPay: number;
    coverageRate: number;
  };
  departmentBreakdown: Array<{
    departmentId: number;
    name: string;
    code: string;
    managerName?: string;
    headcount: number;
    activeHeadcount: number;
    monthlyPayroll: number;
    pendingLeaveRequests: number;
  }>;
  actionItems: Array<{
    severity: string;
    title: string;
    detail: string;
    route: string;
  }>;
  milestones: Array<{
    title: string;
    detail: string;
    date: string;
    route: string;
  }>;
}
