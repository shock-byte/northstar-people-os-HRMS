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

  displayedColumns = ['name', 'email', 'department', 'status', 'actions'];
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
    this.departmentApi.list().subscribe(depts => (this.departments = depts));
    this.load();
  }

  load(): void {
    const filters = this.filterForm.value;
    this.employeeApi
      .list({
        departmentId: filters.departmentId ?? undefined,
        status: filters.status ?? undefined,
        page: 0,
        size: 100
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
    this.filterForm.reset();
    this.load();
  }

  startCreate(): void {
    this.editing = null;
    this.form.reset({
      status: 'ACTIVE',
      hireDate: new Date()
    });
  }

  startEdit(emp: Employee): void {
    this.editing = emp;
    this.form.reset({
      ...emp,
      hireDate: emp.hireDate ? new Date(emp.hireDate) : null
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

    if (this.editing && this.editing.id != null) {
      this.employeeApi.update(this.editing.id, { ...this.editing, ...payload }).subscribe({
        next: () => {
          this.snackBar.open('Employee updated', 'Close', { duration: 2000 });
          this.load();
          this.editing = null;
        }
      });
    } else {
      this.employeeApi.create(payload).subscribe({
        next: () => {
          this.snackBar.open('Employee created', 'Close', { duration: 2000 });
          this.load();
        }
      });
    }
  }

  delete(emp: Employee): void {
    if (!emp.id) {
      return;
    }
    if (!confirm(`Delete employee "${emp.firstName} ${emp.lastName}"?`)) {
      return;
    }
    this.employeeApi.delete(emp.id).subscribe({
      next: () => {
        this.snackBar.open('Employee deleted', 'Close', { duration: 2000 });
        this.load();
      }
    });
  }
}

