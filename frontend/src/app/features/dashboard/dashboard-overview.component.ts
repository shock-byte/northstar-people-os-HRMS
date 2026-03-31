import { Component, OnInit } from '@angular/core';
import { DashboardOverview } from '../../core/models/dashboard.model';
import { DashboardApiService } from '../../core/services/dashboard-api.service';

@Component({
  selector: 'app-dashboard-overview',
  templateUrl: './dashboard-overview.component.html',
  styleUrls: ['./dashboard-overview.component.scss']
})
export class DashboardOverviewComponent implements OnInit {

  overview: DashboardOverview | null = null;
  isLoading = true;

  constructor(private readonly dashboardApi: DashboardApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.dashboardApi.getOverview().subscribe({
      next: overview => {
        this.overview = overview;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  monthLabel(year: number, month: number): string {
    return new Intl.DateTimeFormat('en-IN', {
      month: 'long',
      year: 'numeric'
    }).format(new Date(year, month - 1, 1));
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(value ?? 0);
  }

  formatPercent(value: number): string {
    return `${Math.round(value ?? 0)}%`;
  }

  departmentShare(headcount: number): number {
    if (!this.overview?.summary.totalEmployees) {
      return 0;
    }
    return (headcount / this.overview.summary.totalEmployees) * 100;
  }
}
