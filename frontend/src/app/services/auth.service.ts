import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { ROUTE } from '../enum/routes';
import { LoginRequest } from '../model/login-request';
import { LoginResponse } from '../model/login-response';
import { RegisterRequest } from '../model/register-request';
import { RegisterResponse } from '../model/register-response';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  user: string | undefined;
  token: string | undefined;
  role: string | undefined;
  email: string | undefined;
  authenticated = false;

  http = inject(HttpClient)

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(environment.apiUrl + '/auth' + ROUTE.LOGIN, credentials, { observe: 'response' }).pipe(
      map((response: HttpResponse<LoginResponse>) => {
        if (response.headers.get('Authorization')) {
          this.setToken(response.headers.get('Authorization')!)
        }
        return response.body!;
      })
    )
  }
  register(user: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(environment.apiUrl + '/auth' + ROUTE.REGISTER, user);
  }
  getUser() {
    return this.user;
  }
  setUser(user: string) {
    this.user = user;
  }
  getEmail(): string {
    return this.email!;
  }
  setEmail(email: string): void {
    this.email = email
  }
  getToken() {
    return this.token
  }
  setToken(token: string) {
    try {
      this.parseToken(token);
      if (this.getRole() != 'BUSINESSADMIN') {
        localStorage.setItem('token', token);
      }
    } catch (e) {
      this.token = '';
    }
  }
  parseToken(token: string) {
    const parts = token.split('.');
    if (parts.length !== 3) {
      throw new Error('Invalid token format');
    }
    const payload = JSON.parse(atob(parts[1]));
    this.email = payload.email;
    this.role = payload.roles;
    this.token = token;
    this.authenticated = true;
  }
  getRole(): string {
    return this.role!;
  }
  setRole(roles: string): void {
    this.role = roles;
  }
  isAuthenticated(): boolean {
    return this.authenticated;
  }
}
