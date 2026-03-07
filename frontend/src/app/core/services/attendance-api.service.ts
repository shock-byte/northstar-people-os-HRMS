import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AttendanceRecord } from '../models/attendance.model';

@Injectable({ providedIn: 'root' })
export class AttendanceApiService {

  private readonly baseUrl = `${environment.apiBaseUrl}/attendance`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<AttendanceRecord[]> {
    return this.http.get<AttendanceRecord[]>(this.baseUrl);
  }

  get(id: number): Observable<AttendanceRecord> {
    return this.http.get<AttendanceRecord>(`${this.baseUrl}/${id}`);
  }

  createOrUpdate(payload: AttendanceRecord): Observable<AttendanceRecord> {
    return this.http.post<AttendanceRecord>(this.baseUrl, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}

