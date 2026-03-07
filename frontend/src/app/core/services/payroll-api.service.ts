import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PayrollRecord } from '../models/payroll.model';

@Injectable({ providedIn: 'root' })
export class PayrollApiService {

  private readonly baseUrl = `${environment.apiBaseUrl}/payroll`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<PayrollRecord[]> {
    return this.http.get<PayrollRecord[]>(this.baseUrl);
  }

  get(id: number): Observable<PayrollRecord> {
    return this.http.get<PayrollRecord>(`${this.baseUrl}/${id}`);
  }

  createOrUpdate(payload: PayrollRecord): Observable<PayrollRecord> {
    return this.http.post<PayrollRecord>(this.baseUrl, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}

