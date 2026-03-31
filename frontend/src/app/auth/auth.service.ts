import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthSession } from './auth-session.model';

const TOKEN_KEY = 'hrms_token';
const SESSION_KEY = 'hrms_session';

@Injectable()
export class AuthService {

  private readonly sessionSubject = new BehaviorSubject<AuthSession | null>(this.readStoredSession());

  constructor(private readonly http: HttpClient) {}

  login(username: string, password: string): Observable<AuthSession> {
    const basic = btoa(`${username}:${password}`);
    return this.http
      .get<AuthSession>(`${environment.apiBaseUrl}/auth/session`, {
        headers: new HttpHeaders({
          Authorization: `Basic ${basic}`
        })
      })
      .pipe(tap(session => this.persistSession(basic, session)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(SESSION_KEY);
    this.sessionSubject.next(null);
  }

  get token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  get session(): AuthSession | null {
    return this.sessionSubject.value;
  }

  get sessionChanges(): Observable<AuthSession | null> {
    return this.sessionSubject.asObservable();
  }

  isAuthenticated(): boolean {
    return !!this.token;
  }

  private persistSession(token: string, session: AuthSession): void {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
    this.sessionSubject.next(session);
  }

  private readStoredSession(): AuthSession | null {
    const raw = localStorage.getItem(SESSION_KEY);
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as AuthSession;
    } catch {
      localStorage.removeItem(SESSION_KEY);
      return null;
    }
  }
}
