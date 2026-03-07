export interface PayrollRecord {
  id?: number;
  employeeId: number;
  employeeName?: string;
  year: number;
  month: number;
  baseSalary?: number;
  allowances?: number;
  deductions?: number;
  netPay?: number;
}

