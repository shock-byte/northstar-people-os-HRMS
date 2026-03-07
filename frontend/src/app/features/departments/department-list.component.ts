import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Department } from '../../core/models/department.model';
import { DepartmentApiService } from '../../core/services/department-api.service';

@Component({
  selector: 'app-department-list',
  templateUrl: './department-list.component.html',
  styleUrls: ['./department-list.component.scss']
})
export class DepartmentListComponent implements OnInit {

  displayedColumns = ['name', 'code', 'managerName', 'actions'];
  departments: Department[] = [];
  form: FormGroup;
  editing: Department | null = null;

  constructor(
    private readonly departmentApi: DepartmentApiService,
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
    this.load();
  }

  load(): void {
    this.departmentApi.list().subscribe(depts => {
      this.departments = depts;
    });
  }

  startCreate(): void {
    this.editing = null;
    this.form.reset();
  }

  startEdit(dept: Department): void {
    this.editing = dept;
    this.form.reset(dept);
  }

  save(): void {
    if (this.form.invalid) {
      return;
    }
    const value = this.form.value as Department;
    if (this.editing && this.editing.id != null) {
      this.departmentApi.update(this.editing.id, { ...this.editing, ...value }).subscribe({
        next: () => {
          this.snackBar.open('Department updated', 'Close', { duration: 2000 });
          this.load();
          this.editing = null;
          this.form.reset();
        }
      });
    } else {
      this.departmentApi.create(value).subscribe({
        next: () => {
          this.snackBar.open('Department created', 'Close', { duration: 2000 });
          this.load();
          this.form.reset();
        }
      });
    }
  }

  delete(dept: Department): void {
    if (!dept.id) {
      return;
    }
    if (!confirm(`Delete department "${dept.name}"?`)) {
      return;
    }
    this.departmentApi.delete(dept.id).subscribe({
      next: () => {
        this.snackBar.open('Department deleted', 'Close', { duration: 2000 });
        this.load();
      }
    });
  }
}

