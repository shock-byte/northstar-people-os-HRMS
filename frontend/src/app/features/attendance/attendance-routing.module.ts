import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AttendanceListComponent } from './attendance-list.component';

const routes: Routes = [
  {
    path: '',
    component: AttendanceListComponent,
    data: {
      title: 'Attendance',
      subtitle: 'Capture daily work logs, watch punctuality, and resolve attendance exceptions quickly.'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AttendanceRoutingModule {}
