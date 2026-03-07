import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LeaveRequest, LeaveStatus, LeaveType } from '../../core/models/leave.model';
import { LeaveApiService } from '../../core/services/leave-api.service';
import { Employee } from '../../core/models/employee.model';
import { EmployeeApiService } from '../../core/services/employee-api.service';

@Component({
  selector: 'app-leave-list',
  templateUrl: './leave-list.component.html',
  styleUrls: ['./leave-list.component.scss']
})
export class LeaveListComponent implements OnInit {

  displayedColumns = ['employee', 'period', 'type', 'status', 'actions'];
  leaves: LeaveRequest[] = [];
  employees: Employee[] = [];

  types: LeaveType[] = ['ANNUAL', 'SICK', 'UNPAID', 'OTHER'];
  statuses: LeaveStatus[] = ['PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'];

  form: FormGroup;
  editing: LeaveRequest | null = null;

  constructor(
    private readonly leaveApi: LeaveApiService,
    private readonly employeeApi: EmployeeApiService,
    private readonly fb: FormBuilder,
    private readonly snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      employeeId: [null, Validators.required],
      startDate: [null, Validators.required],
      endDate: [null, Validators.required],
      type: ['ANNUAL' as LeaveType, Validators.required],
      comment: ['']
    });
  }

  ngOnInit(): void {
    this.employeeApi
      .list({ page: 0, size: 200 })
      .subscribe(page => (this.employees = page.content));
    this.load();
  }

  load(): void {
    this.leaveApi.list().subscribe(l => (this.leaves = l));
  }

  startCreate(): void {
    this.editing = null;
    this.form.reset({
      type: 'ANNUAL'
    });
  }

  startEdit(leave: LeaveRequest): void {
    this.editing = leave;
    this.form.reset({
      employeeId: leave.employeeId,
      startDate: new Date(leave.startDate),
      endDate: new Date(leave.endDate),
      type: leave.type,
      comment: leave.comment
    });
  }

  save(): void {
    if (this.form.invalid) {
      return;
    }
    const raw = this.form.value;
    const payload: LeaveRequest = {
      ...this.editing,
      employeeId: raw.employeeId,
      startDate: (raw.startDate as Date).toISOString().substring(0, 10),
      endDate: (raw.endDate as Date).toISOString().substring(0, 10),
      type: raw.type,
      comment: raw.comment
    };

    const obs = this.editing && this.editing.id
      ? this.leaveApi.update(this.editing.id, payload)
      : this.leaveApi.create(payload);

    obs.subscribe({
      next: () => {
        this.snackBar.open('Leave saved', 'Close', { duration: 2000 });
        this.load();
        this.editing = null;
      }
    });
  }

  setStatus(leave: LeaveRequest, status: LeaveStatus): void {
    if (!leave.id) {
      return;
    }
    this.leaveApi.changeStatus(leave.id, status).subscribe({
      next: () => {
        this.snackBar.open('Leave status updated', 'Close', { duration: 2000 });
        this.load();
      }
    });
  }

  delete(leave: LeaveRequest): void {
    if (!leave.id) {
      return;
    }
    if (!confirm('Delete leave request?')) {
      return;
    }
    this.leaveApi.delete(leave.id).subscribe({
      next: () => {
        this.snackBar.open('Leave deleted', 'Close', { duration: 2000 });
        this.load();
      }
    });
  }
}

