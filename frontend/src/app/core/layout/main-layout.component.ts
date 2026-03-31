import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, Subject, takeUntil } from 'rxjs';
import { AuthService } from '../../auth/auth.service';
import { AuthSession } from '../../auth/auth-session.model';

interface NavItem {
  route: string;
  label: string;
  description: string;
}

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent implements OnInit, OnDestroy {

  readonly navItems: NavItem[] = [
    {
      route: '/dashboard',
      label: 'Dashboard',
      description: 'Executive summary, approvals, and workforce health'
    },
    {
      route: '/employees',
      label: 'Employees',
      description: 'People directory, hiring velocity, and role data'
    },
    {
      route: '/departments',
      label: 'Departments',
      description: 'Org structure, managers, and staffing distribution'
    },
    {
      route: '/leave',
      label: 'Leave',
      description: 'Requests, approvals, and upcoming time away'
    },
    {
      route: '/attendance',
      label: 'Attendance',
      description: 'Daily logs, exceptions, and punctuality trends'
    },
    {
      route: '/payroll',
      label: 'Payroll',
      description: 'Compensation runs, deductions, and payout coverage'
    }
  ];

  session: AuthSession | null = this.authService.session;
  isMobile = false;
  pageTitle = 'Dashboard';
  pageSubtitle = 'Track workforce health, approvals, and payroll readiness from one workspace.';
  readonly todayLabel = new Intl.DateTimeFormat('en-IN', {
    weekday: 'long',
    month: 'long',
    day: 'numeric'
  }).format(new Date());
  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private readonly breakpointObserver: BreakpointObserver
  ) {}

  ngOnInit(): void {
    this.authService.sessionChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(session => {
        this.session = session;
      });

    this.breakpointObserver
      .observe('(max-width: 960px)')
      .pipe(takeUntil(this.destroy$))
      .subscribe(result => {
        this.isMobile = result.matches;
      });

    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe(() => this.syncRouteMeta());

    this.syncRouteMeta();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  logout(): void {
    this.authService.logout();
    void this.router.navigate(['/login']);
  }

  get roleLabel(): string {
    return this.session?.roles.length ? this.session.roles.join(' / ') : 'Signed in';
  }

  get initials(): string {
    return (this.session?.displayName ?? 'HR')
      .split(/\s+/)
      .filter(Boolean)
      .slice(0, 2)
      .map(part => part.charAt(0).toUpperCase())
      .join('');
  }

  private syncRouteMeta(): void {
    let route = this.activatedRoute;
    while (route.firstChild) {
      route = route.firstChild;
    }

    this.pageTitle = route.snapshot.data['title'] ?? 'Dashboard';
    this.pageSubtitle = route.snapshot.data['subtitle']
      ?? 'Track workforce health, approvals, and payroll readiness from one workspace.';
  }
}
