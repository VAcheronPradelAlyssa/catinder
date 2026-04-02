import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly http = inject(HttpClient);
  private readonly storageKey = 'catinder_jwt';

  login(payload: LoginPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.API_URL}/auth/login`, payload).pipe(
      tap(response => this.persistSession(response.token, payload.rememberMe ?? true)),
    );
  }

  register(payload: RegisterPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.API_URL}/auth/register`, payload).pipe(
      tap(response => this.persistSession(response.token, true)),
    );
  }

  getToken(): string | null {
    return localStorage.getItem(this.storageKey) ?? sessionStorage.getItem(this.storageKey);
  }

  isAuthenticated(): boolean {
    return Boolean(this.getToken());
  }

  authHeaders(): HttpHeaders {
    const token = this.getToken();

    return new HttpHeaders(token ? { Authorization: `Bearer ${token}` } : {});
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
    sessionStorage.removeItem(this.storageKey);
  }

  private persistSession(token: string, rememberMe: boolean): void {
    this.logout();

    const storage = rememberMe ? localStorage : sessionStorage;
    storage.setItem(this.storageKey, token);
  }
}

export interface LoginPayload {
  email: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterPayload {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user?: {
    id?: string | number;
    username?: string;
    email?: string;
  };
}
