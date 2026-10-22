import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface EmailRequest {
  to: string;
  subject: string;
  message: string;
}

export interface ApiResponse {
  status: string;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class EmailService {
  private http = inject(HttpClient);

  send(request: EmailRequest): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${environment.apiUrl}/api/emails/send`, request);
  }
}
