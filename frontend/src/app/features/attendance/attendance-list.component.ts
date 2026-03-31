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
      .list({ page: 0, size: 300 })
      .subscribe(page => (this.employees = page.content));
    this.load();
  }

  load(): void {
    this.attendanceApi.list().subscribe(records => {
      this.records = [...records].sort((left, right) => right.workDate.localeCompare(left.workDate));
    });
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
        this.snackBar.open('Attendance saved', 'Close', { duration: 2500 });
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
        this.snackBar.open('Attendance deleted', 'Close', { duration: 2500 });
        this.load();
      }
    });
  }

  get todayRecords(): AttendanceRecord[] {
    const today = new Date().toISOString().substring(0, 10);
    return this.records.filter(record => record.workDate === today);
  }

  get presentCount(): number {
    return this.todayRecords.filter(record => record.status === 'PRESENT' || record.status === 'HALF_DAY').length;
  }

  get lateCount(): number {
    return this.todayRecords.filter(record => record.status === 'LATE').length;
  }

  get absentCount(): number {
    return this.todayRecords.filter(record => record.status === 'ABSENT').length;
  }

  get averageCheckIn(): string {
    const checkIns = this.todayRecords
      .map(record => record.checkInTime)
      .filter((value): value is string => !!value);

    if (!checkIns.length) {
      return 'No check-ins yet';
    }

    const totalMinutes = checkIns.reduce((sum, time) => {
      const [hours, minutes] = time.split(':').map(Number);
      return sum + (hours * 60) + minutes;
    }, 0);

    const averageMinutes = Math.round(totalMinutes / checkIns.length);
    const hours = Math.floor(averageMinutes / 60).toString().padStart(2, '0');
    const minutes = (averageMinutes % 60).toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  statusClass(status: AttendanceStatus): string {
    return `status-pill--${status.toLowerCase()}`;
  }
}
