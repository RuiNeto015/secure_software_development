import { TestBed, fakeAsync, flush, tick } from '@angular/core/testing';

import { RateLimitingService } from './rate-limiting.service';

describe('RateLimitingService', () => {
  let service: RateLimitingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RateLimitingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return false if capacity is at 0', fakeAsync(() => {
    service.setCapacity(0);

    expect(service.tryConsume()).toBeFalse();
    flush();
  }))

  it('should return false if capacity is above 0', fakeAsync(() => {
    expect(service.tryConsume()).toBeTrue();

    flush();
  }))

  it('should limit the amount of requests allowed per minute to 2', fakeAsync(() => {
    service.setCapacity(2);

    // consume all requests
    expect(service.tryConsume()).toBeTrue();
    expect(service.tryConsume()).toBeTrue();
    expect(service.tryConsume()).toBeFalse();

    // elapse 1 minute
    tick(1000 * 60);

    // should have refilled
    expect(service.tryConsume()).toBeTrue();

    flush();
  }))
});
