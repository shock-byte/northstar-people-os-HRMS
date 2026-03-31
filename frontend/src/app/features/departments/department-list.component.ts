import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { forkJoin } from 'rxjs';
import { Department } from '../../core/models/department.model';
import { Employee } from '../../core/models/employee.model';
import { DepartmentApiService } from '../../core/services/department-api.service';
import { EmployeeApiService } from '../../core/services/employee-api.service';

@Component({
  selector: 'app-department-list',
  templateUrl: './department-list.component.html',
  styleUrls: ['./department-list.component.scss']
})
export class DepartmentListComponent implements OnInit {

  displayedColumns = ['department', 'manager', 'headcount', 'payroll', 'actions'];
  departments: Department[] = [];
  employees: Employee[] = [];
  form: FormGroup;
  editing: Department | null = null;

  constructor(
    private readonly departmentApi: DepartmentApiService,
    private readonly employeeApi: EmployeeApiService,
    private readonly fb: FormBuilder,
    private readonly snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      code: ['', Validators.required],
      description: [''],
      managerName: ['']
    });
  }

  ngOnInit(): void {
    this.startCreate();
    this.load();
  }

  load(): void {
    forkJoin({
      departments: this.departmentApi.list(),
      employees: this.employeeApi.list({ page: 0, size: 300 })
    }).subscribe(({ departments, employees }) => {
      this.departments = departments;
      this.employees = employees.content;
    });
  }

  startCreate(): void {
    this.editing = null;
    this.form.reset();
  }

  startEdit(department: Department): void {
    this.editing = department;
    this.form.reset(department);
  }

  save(): void {
    if (this.form.invalid) {
      return;
    }
    const value = this.form.value as Department;
    const request = this.editing?.id
      ? this.departmentApi.update(this.editing.id, { ...this.editing, ...value })
      : this.departmentApi.create(value);

    request.subscribe({
      next: () => {
        this.snackBar.open(this.editing ? 'Department updated' : 'Department created', 'Close', {
          duration: 2500
        });
        this.load();
        this.startCreate();
      }
    });
  }

  delete(department: Department): void {
    if (!department.id) {
      return;
    }
    if (!confirm(`Delete department "${department.name}"?`)) {
      return;
    }
    this.departmentApi.delete(department.id).subscribe({
      next: () => {
        this.snackBar.open('Department deleted', 'Close', { duration: 2500 });
        this.load();
      }
    });
  }

  get staffedDepartments(): number {
    return this.departments.filter(department => this.headcountFor(department) > 0).length;
  }

  get managersAssigned(): number {
    return this.departments.filter(department => !!department.managerName).length;
  }

  get averageTeamSize(): number {
    if (!this.departments.length) {
      return 0;
    }
    return Math.round(this.employees.length / this.departments.length);
  }

  get totalPayroll(): number {
    return this.employees.reduce((sum, employee) => sum + (employee.monthlySalary ?? 0), 0);
  }

  headcountFor(department: Department): number {
    return this.employees.filter(employee => employee.departmentId === department.id).length;
  }

  payrollFor(department: Department): number {
    return this.employees
      .filter(employee => employee.departmentId === department.id)
      .reduce((sum, employee) => sum + (employee.monthlySalary ?? 0), 0);
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(value ?? 0);
  }
}
