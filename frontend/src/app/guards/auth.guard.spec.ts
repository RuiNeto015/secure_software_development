import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

describe('authGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => authGuard([])(...guardParameters));
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authService = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'getRoles']);
    router = jasmine.createSpyObj('Router', ['navigateByUrl']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    });
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });

  xit('should redirect to login if not authenticated', () => {
    authService.isAuthenticated.and.returnValue(false);

    expect(executeGuard).toBeFalse();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
  });

  xit('should allow access if no roles are required', () => {
    authService.isAuthenticated.and.returnValue(true);

    expect(executeGuard).toBeTrue();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  xit('should allow access if user has required role', () => {
    authService.isAuthenticated.and.returnValue(true);
    authService.getRole.and.returnValue('admin');
    const guard = authGuard(['admin']);
    // const canActivate = guard();

    expect(executeGuard).toBeTrue();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  xit('should deny access if user does not have required role', () => {
    authService.isAuthenticated.and.returnValue(true);
    authService.getRole.and.returnValue('user');
    const guard = authGuard(['admin']);
    // const canActivate = guard();

    expect(executeGuard).toBeFalse();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
  });
});
