import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PayrollListComponent } from './payroll-list.component';

const routes: Routes = [
  {
    path: '',
    component: PayrollListComponent,
    data: {
      title: 'Payroll',
      subtitle: 'Run compensation cycles, monitor coverage, and validate net pay before every payout window.'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PayrollRoutingModule {}
