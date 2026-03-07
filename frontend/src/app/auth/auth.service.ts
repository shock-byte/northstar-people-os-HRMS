import { Injectable } from '@angular/core';

const TOKEN_KEY = 'hrms_token';

@Injectable()
export class AuthService {

  login(username: string, password: string): void {
    const basic = btoa(`${username}:${password}`);
    localStorage.setItem(TOKEN_KEY, basic);
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
  }

  get token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.token;
  }
}

