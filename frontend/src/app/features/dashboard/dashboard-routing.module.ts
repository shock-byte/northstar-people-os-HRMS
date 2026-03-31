import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardOverviewComponent } from './dashboard-overview.component';

const routes: Routes = [
  {
    path: '',
    component: DashboardOverviewComponent,
    data: {
      title: 'Dashboard',
      subtitle: 'Watch headcount, approvals, attendance coverage, and payroll readiness in real time.'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule {}
