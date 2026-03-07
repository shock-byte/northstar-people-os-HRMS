export type EmploymentStatus = 'ACTIVE' | 'INACTIVE' | 'TERMINATED';

export interface Employee {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  hireDate: string;
  jobTitle?: string;
  status: EmploymentStatus;
  departmentId?: number;
  departmentName?: string;
  monthlySalary?: number;
}

