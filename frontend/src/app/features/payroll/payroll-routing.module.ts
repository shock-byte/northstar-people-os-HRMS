import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PayrollListComponent } from './payroll-list.component';

const routes: Routes = [
  {
    path: '',
    component: PayrollListComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PayrollRoutingModule {}

