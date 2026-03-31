import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PayrollRecord } from '../../core/models/payroll.model';
import { PayrollApiService } from '../../core/services/payroll-api.service';
import { Employee } from '../../core/models/employee.model';
import { EmployeeApiService } from '../../core/services/employee-api.service';

@Component({
  selector: 'app-payroll-list',
  templateUrl: './payroll-list.component.html',
  styleUrls: ['./payroll-list.component.scss']
})
export class PayrollListComponent implements OnInit {

  displayedColumns = ['employee', 'period', 'baseSalary', 'allowances', 'deductions', 'netPay', 'actions'];
  records: PayrollRecord[] = [];
  employees: Employee[] = [];

  form: FormGroup;

  constructor(
    private readonly payrollApi: PayrollApiService,
    private readonly employeeApi: EmployeeApiService,
    private readonly fb: FormBuilder,
    private readonly snackBar: MatSnackBar
  ) {
    const now = new Date();
    this.form = this.fb.group({
      employeeId: [null, Validators.required],
      year: [now.getFullYear(), [Validators.required]],
      month: [now.getMonth() + 1, [Validators.required]],
      baseSalary: [null],
      allowances: [0],
      deductions: [0]
    });
  }

  ngOnInit(): void {
    this.employeeApi
      .list({ page: 0, size: 300 })
      .subscribe(page => (this.employees = page.content));
    this.load();
  }

  load(): void {
    this.payrollApi.list().subscribe(records => {
      this.records = [...records].sort((left, right) => {
        if (left.year !== right.year) {
          return right.year - left.year;
        }
        return right.month - left.month;
      });
    });
  }

  syncBaseSalary(): void {
    const employeeId = this.form.get('employeeId')?.value as number | null;
    const employee = this.employees.find(candidate => candidate.id === employeeId);
    if (!employee) {
      return;
    }
    this.form.patchValue({
      baseSalary: employee.monthlySalary ?? this.form.get('baseSalary')?.value
    });
  }

  save(): void {
    if (this.form.invalid) {
      return;
    }
    const payload: PayrollRecord = this.form.value;
    this.payrollApi.createOrUpdate(payload).subscribe({
      next: () => {
        this.snackBar.open('Payroll saved', 'Close', { duration: 2500 });
        this.load();
      }
    });
  }

  delete(record: PayrollRecord): void {
    if (!record.id) {
      return;
    }
    if (!confirm('Delete payroll record?')) {
      return;
    }
    this.payrollApi.delete(record.id).subscribe({
      next: () => {
        this.snackBar.open('Payroll deleted', 'Close', { duration: 2500 });
        this.load();
      }
    });
  }

  get currentMonthRecords(): PayrollRecord[] {
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth() + 1;
    return this.records.filter(record => record.year === currentYear && record.month === currentMonth);
  }

  get processedCount(): number {
    return this.currentMonthRecords.length;
  }

  get totalNetPay(): number {
    return this.currentMonthRecords.reduce((sum, record) => sum + (record.netPay ?? 0), 0);
  }

  get totalDeductions(): number {
    return this.currentMonthRecords.reduce((sum, record) => sum + (record.deductions ?? 0), 0);
  }

  get averageNetPay(): number {
    return this.currentMonthRecords.length
      ? this.totalNetPay / this.currentMonthRecords.length
      : 0;
  }

  formatCurrency(value?: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(value ?? 0);
  }
}
