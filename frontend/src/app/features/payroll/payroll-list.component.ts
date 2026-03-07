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
      .list({ page: 0, size: 200 })
      .subscribe(page => (this.employees = page.content));
    this.load();
  }

  load(): void {
    this.payrollApi.list().subscribe(r => (this.records = r));
  }

  save(): void {
    if (this.form.invalid) {
      return;
    }
    const payload: PayrollRecord = this.form.value;
    this.payrollApi.createOrUpdate(payload).subscribe({
      next: () => {
        this.snackBar.open('Payroll saved', 'Close', { duration: 2000 });
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
        this.snackBar.open('Payroll deleted', 'Close', { duration: 2000 });
        this.load();
      }
    });
  }
}

