import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './core/layout/main-layout.component';
import { LoginComponent } from './auth/login.component';
import { AuthGuard } from './auth/auth.guard';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    data: {
      title: 'Sign In',
      subtitle: 'Authenticate against the backend and open the HR workspace.'
    }
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./features/dashboard/dashboard.module').then(m => m.DashboardModule)
      },
      {
        path: 'employees',
        loadChildren: () =>
          import('./features/employees/employees.module').then(m => m.EmployeesModule)
      },
      {
        path: 'departments',
        loadChildren: () =>
          import('./features/departments/departments.module').then(m => m.DepartmentsModule)
      },
      {
        path: 'leave',
        loadChildren: () =>
          import('./features/leave/leave.module').then(m => m.LeaveModule)
      },
      {
        path: 'attendance',
        loadChildren: () =>
          import('./features/attendance/attendance.module').then(m => m.AttendanceModule)
      },
      {
        path: 'payroll',
        loadChildren: () =>
          import('./features/payroll/payroll.module').then(m => m.PayrollModule)
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
