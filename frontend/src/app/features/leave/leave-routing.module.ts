import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LeaveListComponent } from './leave-list.component';

const routes: Routes = [
  {
    path: '',
    component: LeaveListComponent,
    data: {
      title: 'Leave',
      subtitle: 'Review upcoming time away, approve requests, and keep staffing plans ahead of schedule.'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LeaveRoutingModule {}
