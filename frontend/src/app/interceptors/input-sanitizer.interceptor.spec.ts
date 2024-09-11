import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn } from '@angular/common/http';

import { inputSanitizerInterceptor } from './input-sanitizer.interceptor';

describe('inputSanitizerInterceptor', () => {
  const interceptor: HttpInterceptorFn = (req, next) => 
    TestBed.runInInjectionContext(() => inputSanitizerInterceptor(req, next));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  it('should return an error response when input is too large', () => {
    const req = { body: 'a'.repeat(2 * 1024 * 1024 + 1) };
    const next = jasmine.createSpyObj('next', ['handle']);
    interceptor(req as any, next);
    expect(next.handle).not.toHaveBeenCalled();
  });

  it('should return an error response when input contains XSS tags', () => {
    const req = { body: '<script>alert("XSS")</script>' };
    const next = jasmine.createSpyObj('next', ['handle']);
    interceptor(req as any, next);
    expect(next.handle).not.toHaveBeenCalled();
  });

  it('should return an error response when input contains SQL injection keywords', () => {
    const req = { body: 'SELECT * FROM users' };
    const next = jasmine.createSpyObj('next', ['handle']);
    interceptor(req as any, next);
    expect(next.handle).not.toHaveBeenCalled();
  });
});
