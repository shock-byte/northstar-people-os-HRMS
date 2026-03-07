import { Component } from '@angular/core';
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
  isTypingUsername = false;
  isPasswordFocus = false;

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
    if (this.form.invalid) {
      return;
    }
    const { username, password } = this.form.value;
    this.authService.login(username, password);
    this.snackBar.open('Logged in', 'Close', { duration: 2000 });
    void this.router.navigate(['/']);
  }

  onUsernameInput(): void {
    this.isTypingUsername = true;
    this.isPasswordFocus = false;
  }

  onUsernameFocus(): void {
    this.isTypingUsername = true;
  }

  onUsernameBlur(): void {
    this.isTypingUsername = false;
  }

  onPasswordFocus(): void {
    this.isPasswordFocus = true;
    this.isTypingUsername = false;
  }

  onPasswordBlur(): void {
    this.isPasswordFocus = false;
  }
}

