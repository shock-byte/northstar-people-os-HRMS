import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LeaveRequest, LeaveStatus } from '../models/leave.model';

@Injectable({ providedIn: 'root' })
export class LeaveApiService {

  private readonly baseUrl = `${environment.apiBaseUrl}/leave-requests`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<LeaveRequest[]> {
    return this.http.get<LeaveRequest[]>(this.baseUrl);
  }

  get(id: number): Observable<LeaveRequest> {
    return this.http.get<LeaveRequest>(`${this.baseUrl}/${id}`);
  }

  create(payload: LeaveRequest): Observable<LeaveRequest> {
    return this.http.post<LeaveRequest>(this.baseUrl, payload);
  }

  update(id: number, payload: LeaveRequest): Observable<LeaveRequest> {
    return this.http.put<LeaveRequest>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  changeStatus(id: number, status: LeaveStatus, approverName?: string): Observable<LeaveRequest> {
    let params = new HttpParams().set('status', status);
    if (approverName) {
      params = params.set('approverName', approverName);
    }
    return this.http.patch<LeaveRequest>(`${this.baseUrl}/${id}/status`, null, { params });
  }
}

