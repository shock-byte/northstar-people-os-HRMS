import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Employee, EmploymentStatus } from '../models/employee.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class EmployeeApiService {

  private readonly baseUrl = `${environment.apiBaseUrl}/employees`;

  constructor(private readonly http: HttpClient) {}

  list(options?: { departmentId?: number; status?: EmploymentStatus; query?: string; page?: number; size?: number }): Observable<Page<Employee>> {
    let params = new HttpParams();
    if (options?.departmentId != null) {
      params = params.set('departmentId', options.departmentId);
    }
    if (options?.status != null) {
      params = params.set('status', options.status);
    }
    if (options?.query) {
      params = params.set('query', options.query);
    }
    if (options?.page != null) {
      params = params.set('page', options.page);
    }
    if (options?.size != null) {
      params = params.set('size', options.size);
    }
    return this.http.get<Page<Employee>>(this.baseUrl, { params });
  }

  get(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.baseUrl}/${id}`);
  }

  create(payload: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.baseUrl, payload);
  }

  update(id: number, payload: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
