import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EmployeeListComponent } from './employee-list.component';

const routes: Routes = [
  {
    path: '',
    component: EmployeeListComponent,
    data: {
      title: 'Employees',
      subtitle: 'Manage the people directory, assignments, employment status, and compensation bands.'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EmployeesRoutingModule {}
