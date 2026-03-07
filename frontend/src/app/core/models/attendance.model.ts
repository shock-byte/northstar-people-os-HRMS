export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE' | 'HALF_DAY';

export interface AttendanceRecord {
  id?: number;
  employeeId: number;
  employeeName?: string;
  workDate: string;
  checkInTime?: string;
  checkOutTime?: string;
  status: AttendanceStatus;
}

