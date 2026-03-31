import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Employee, EmploymentStatus } from '../../core/models/employee.model';
import { Department } from '../../core/models/department.model';
import { EmployeeApiService } from '../../core/services/employee-api.service';
import { DepartmentApiService } from '../../core/services/department-api.service';
import { Page } from '../../core/models/page.model';

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit {

  displayedColumns = ['person', 'jobTitle', 'department', 'salary', 'status', 'actions'];
  data: Employee[] = [];
  total = 0;

  departments: Department[] = [];
  statuses: EmploymentStatus[] = ['ACTIVE', 'INACTIVE', 'TERMINATED'];

  filterForm: FormGroup;
  form: FormGroup;
  editing: Employee | null = null;

  constructor(
    private readonly employeeApi: EmployeeApiService,
    private readonly departmentApi: DepartmentApiService,
    private readonly fb: FormBuilder,
    private readonly snackBar: MatSnackBar
  ) {
    this.filterForm = this.fb.group({
      query: [''],
      departmentId: [null],
      status: [null]
    });

    this.form = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      hireDate: ['', Validators.required],
      jobTitle: [''],
      status: ['ACTIVE' as EmploymentStatus, Validators.required],
      departmentId: [null],
      monthlySalary: [null]
    });
  }

  ngOnInit(): void {
    this.departmentApi.list().subscribe(departments => (this.departments = departments));
    this.startCreate();
    this.load();
  }

  load(): void {
    const filters = this.filterForm.value;
    this.employeeApi
      .list({
        query: filters.query?.trim() || undefined,
        departmentId: filters.departmentId ?? undefined,
        status: filters.status ?? undefined,
        page: 0,
        size: 200
      })
      .subscribe((page: Page<Employee>) => {
        this.data = page.content;
        this.total = page.totalElements;
      });
  }

  applyFilters(): void {
    this.load();
  }

  clearFilters(): void {
    this.filterForm.reset({
      query: '',
      departmentId: null,
      status: null
    });
    this.load();
  }

  startCreate(): void {
    this.editing = null;
    this.form.reset({
      status: 'ACTIVE',
      hireDate: new Date()
    });
  }

  startEdit(employee: Employee): void {
    this.editing = employee;
    this.form.reset({
      ...employee,
      hireDate: employee.hireDate ? new Date(employee.hireDate) : null
    });
  }

  save(): void {
    if (this.form.invalid) {
      return;
    }

    const raw = this.form.value;
    const payload: Employee = {
      ...raw,
      hireDate: (raw.hireDate as Date).toISOString().substring(0, 10)
    };

    const request = this.editing?.id
      ? this.employeeApi.update(this.editing.id, { ...this.editing, ...payload })
      : this.employeeApi.create(payload);

    request.subscribe({
      next: () => {
        this.snackBar.open(
          this.editing ? 'Employee updated' : 'Employee created',
          'Close',
          { duration: 2500 }
        );
        this.load();
        this.startCreate();
      }
    });
  }

  delete(employee: Employee): void {
    if (!employee.id) {
      return;
    }
    if (!confirm(`Delete employee "${employee.firstName} ${employee.lastName}"?`)) {
      return;
    }
    this.employeeApi.delete(employee.id).subscribe({
      next: () => {
        this.snackBar.open('Employee deleted', 'Close', { duration: 2500 });
        this.load();
      }
    });
  }

  get activeCount(): number {
    return this.data.filter(employee => employee.status === 'ACTIVE').length;
  }

  get inactiveCount(): number {
    return this.data.filter(employee => employee.status !== 'ACTIVE').length;
  }

  get newHiresCount(): number {
    const threshold = new Date();
    threshold.setMonth(threshold.getMonth() - 3);
    return this.data.filter(employee => new Date(employee.hireDate) >= threshold).length;
  }

  get monthlyPayroll(): number {
    return this.data.reduce((sum, employee) => sum + (employee.monthlySalary ?? 0), 0);
  }

  formatCurrency(value?: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(value ?? 0);
  }

  statusClass(status: EmploymentStatus): string {
    return `status-pill--${status.toLowerCase()}`;
  }
}
