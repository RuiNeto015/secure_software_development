import { HttpHandlerFn, HttpInterceptorFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { RateLimitingService } from '../services/rate-limiting.service';

export const rateLimitingInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  const service = inject(RateLimitingService)
  if (service.tryConsume()) {
    return next(req);
  } else {
    return new Observable<HttpResponse<any>>(observer => {
      observer.error(new HttpResponse({
        status: 429,
        statusText: 'Too Many Requests',
        body: { error: 'Rate Limit Exceeded' }
      }));
    });
  }
};
