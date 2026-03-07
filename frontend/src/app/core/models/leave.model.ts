export type LeaveType = 'ANNUAL' | 'SICK' | 'UNPAID' | 'OTHER';
export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';

export interface LeaveRequest {
  id?: number;
  employeeId: number;
  employeeName?: string;
  startDate: string;
  endDate: string;
  type: LeaveType;
  status?: LeaveStatus;
  approverName?: string;
  comment?: string;
}

