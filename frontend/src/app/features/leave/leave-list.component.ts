import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LeaveRequest, LeaveStatus, LeaveType } from '../../core/models/leave.model';
import { LeaveApiService } from '../../core/services/leave-api.service';
import { Employee } from '../../core/models/employee.model';
import { EmployeeApiService } from '../../core/services/employee-api.service';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-leave-list',
  templateUrl: './leave-list.component.html',
  styleUrls: ['./leave-list.component.scss']
})
export class LeaveListComponent implements OnInit {

  displayedColumns = ['employee', 'period', 'type', 'status', 'approver', 'actions'];
  leaves: LeaveRequest[] = [];
  employees: Employee[] = [];

  types: LeaveType[] = ['ANNUAL', 'SICK', 'UNPAID', 'OTHER'];
  statuses: LeaveStatus[] = ['PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'];

  form: FormGroup;
  editing: LeaveRequest | null = null;

  constructor(
    private readonly leaveApi: LeaveApiService,
    private readonly employeeApi: EmployeeApiService,
    private readonly authService: AuthService,
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
      .list({ page: 0, size: 300 })
      .subscribe(page => (this.employees = page.content));
    this.startCreate();
    this.load();
  }

  load(): void {
    this.leaveApi.list().subscribe(leaves => {
      this.leaves = [...leaves].sort((left, right) => right.startDate.localeCompare(left.startDate));
    });
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
      ...(this.editing?.id ? { id: this.editing.id } : {}),
      employeeId: raw.employeeId,
      startDate: (raw.startDate as Date).toISOString().substring(0, 10),
      endDate: (raw.endDate as Date).toISOString().substring(0, 10),
      type: raw.type,
      comment: raw.comment
    };

    const request = this.editing?.id
      ? this.leaveApi.update(this.editing.id, payload)
      : this.leaveApi.create(payload);

    request.subscribe({
      next: () => {
        this.snackBar.open(this.editing ? 'Leave request updated' : 'Leave request created', 'Close', {
          duration: 2500
        });
        this.load();
        this.startCreate();
      }
    });
  }

  setStatus(leave: LeaveRequest, status: LeaveStatus): void {
    if (!leave.id) {
      return;
    }
    this.leaveApi.changeStatus(leave.id, status, this.authService.session?.displayName).subscribe({
      next: () => {
        this.snackBar.open('Leave status updated', 'Close', { duration: 2500 });
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
        this.snackBar.open('Leave deleted', 'Close', { duration: 2500 });
        this.load();
      }
    });
  }

  get pendingCount(): number {
    return this.leaves.filter(leave => leave.status === 'PENDING').length;
  }

  get approvedThisMonth(): number {
    const currentMonth = new Date().toISOString().slice(0, 7);
    return this.leaves.filter(leave => leave.status === 'APPROVED' && leave.startDate.startsWith(currentMonth)).length;
  }

  get onLeaveToday(): number {
    const today = new Date().toISOString().substring(0, 10);
    return this.leaves.filter(leave =>
      leave.status === 'APPROVED' &&
      leave.startDate <= today &&
      leave.endDate >= today
    ).length;
  }

  get rejectedCount(): number {
    return this.leaves.filter(leave => leave.status === 'REJECTED').length;
  }

  statusClass(status?: LeaveStatus): string {
    return `status-pill--${(status ?? 'PENDING').toLowerCase()}`;
  }
}
