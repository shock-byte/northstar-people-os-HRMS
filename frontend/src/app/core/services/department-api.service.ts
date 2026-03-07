import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Department } from '../models/department.model';

@Injectable({ providedIn: 'root' })
export class DepartmentApiService {

  private readonly baseUrl = `${environment.apiBaseUrl}/departments`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<Department[]> {
    return this.http.get<Department[]>(this.baseUrl);
  }

  get(id: number): Observable<Department> {
    return this.http.get<Department>(`${this.baseUrl}/${id}`);
  }

  create(payload: Department): Observable<Department> {
    return this.http.post<Department>(this.baseUrl, payload);
  }

  update(id: number, payload: Department): Observable<Department> {
    return this.http.put<Department>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}

