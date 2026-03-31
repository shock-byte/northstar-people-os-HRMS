import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  form: FormGroup;
  isSubmitting = false;
  isPasswordFocus = false;
  readonly demoAccounts = [
    { username: 'admin', password: 'admin123', role: 'Admin workspace' },
    { username: 'hr', password: 'hr123', role: 'People operations' },
    { username: 'employee', password: 'emp123', role: 'Employee self-service' }
  ];

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  submit(): void {
    if (this.form.invalid || this.isSubmitting) {
      return;
    }
    const { username, password } = this.form.value;
    this.isSubmitting = true;
    this.authService.login(username, password).subscribe({
      next: session => {
        this.isSubmitting = false;
        this.snackBar.open(`Welcome back, ${session.displayName}`, 'Close', { duration: 2500 });
        void this.router.navigate(['/dashboard']);
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        const message = error.status === 401
          ? 'That username and password pair was not accepted.'
          : 'Sign-in failed. Please check that the backend is running.';
        this.snackBar.open(message, 'Close', { duration: 3500 });
      }
    });
  }

  onPasswordFocus(): void {
    this.isPasswordFocus = true;
  }

  onPasswordBlur(): void {
    this.isPasswordFocus = false;
  }
}
