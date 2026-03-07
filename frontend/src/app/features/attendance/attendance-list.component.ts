import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AttendanceRecord, AttendanceStatus } from '../../core/models/attendance.model';
import { AttendanceApiService } from '../../core/services/attendance-api.service';
import { Employee } from '../../core/models/employee.model';
import { EmployeeApiService } from '../../core/services/employee-api.service';

@Component({
  selector: 'app-attendance-list',
  templateUrl: './attendance-list.component.html',
  styleUrls: ['./attendance-list.component.scss']
})
export class AttendanceListComponent implements OnInit {

  displayedColumns = ['employee', 'date', 'status', 'checkIn', 'checkOut', 'actions'];
  records: AttendanceRecord[] = [];
  employees: Employee[] = [];

  statuses: AttendanceStatus[] = ['PRESENT', 'ABSENT', 'LATE', 'HALF_DAY'];

  form: FormGroup;

  constructor(
    private readonly attendanceApi: AttendanceApiService,
    private readonly employeeApi: EmployeeApiService,
    private readonly fb: FormBuilder,
    private readonly snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      employeeId: [null, Validators.required],
      workDate: [new Date(), Validators.required],
      status: ['PRESENT' as AttendanceStatus, Validators.required],
      checkInTime: [''],
      checkOutTime: ['']
    });
  }

  ngOnInit(): void {
    this.employeeApi
      .list({ page: 0, size: 200 })
      .subscribe(page => (this.employees = page.content));
    this.load();
  }

  load(): void {
    this.attendanceApi.list().subscribe(r => (this.records = r));
  }

  save(): void {
    if (this.form.invalid) {
      return;
    }
    const raw = this.form.value;
    const payload: AttendanceRecord = {
      employeeId: raw.employeeId,
      workDate: (raw.workDate as Date).toISOString().substring(0, 10),
      status: raw.status,
      checkInTime: raw.checkInTime || undefined,
      checkOutTime: raw.checkOutTime || undefined
    };
    this.attendanceApi.createOrUpdate(payload).subscribe({
      next: () => {
        this.snackBar.open('Attendance saved', 'Close', { duration: 2000 });
        this.load();
      }
    });
  }

  delete(record: AttendanceRecord): void {
    if (!record.id) {
      return;
    }
    if (!confirm('Delete attendance record?')) {
      return;
    }
    this.attendanceApi.delete(record.id).subscribe({
      next: () => {
        this.snackBar.open('Attendance deleted', 'Close', { duration: 2000 });
        this.load();
      }
    });
  }
}

