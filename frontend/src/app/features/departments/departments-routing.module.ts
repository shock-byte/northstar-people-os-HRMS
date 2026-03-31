import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DepartmentListComponent } from './department-list.component';

const routes: Routes = [
  {
    path: '',
    component: DepartmentListComponent,
    data: {
      title: 'Departments',
      subtitle: 'Monitor org structure, department ownership, staffing density, and payroll distribution.'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DepartmentsRoutingModule {}
