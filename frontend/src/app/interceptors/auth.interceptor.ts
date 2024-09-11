import { HttpHeaders, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService)

  const appendHeaderIfMissing = (headers: HttpHeaders, header: string, value: string): HttpHeaders => {
    if (headers.has(header)) return headers
    return headers.append(header, value);
  }

  let headers = req.headers;

  headers = appendHeaderIfMissing(headers, 'Pragma', 'no-cache')
  headers = appendHeaderIfMissing(headers, 'Cache-Control', 'no-cache')

  if (authService.getUser()) {
    headers = appendHeaderIfMissing(headers, 'user', authService.getUser()!);
  }
  if (authService.getToken()) {
    headers = appendHeaderIfMissing(headers, 'Authorization', 'Bearer ' + authService.getToken())
  }

  return next(req.clone({ headers }));
};