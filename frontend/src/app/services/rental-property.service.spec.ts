import { TestBed } from '@angular/core/testing';
import { RentalPropertyService } from './rental-property.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('RentalPropertyService', () => {
  let service: RentalPropertyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ]});
    service = TestBed.inject(RentalPropertyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
