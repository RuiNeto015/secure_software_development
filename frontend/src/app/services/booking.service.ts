import { Injectable, inject } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';
import { RentalProperty } from '../model/rental-property'; 
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ROUTE } from '../enum/routes';
import { BookPropertyRequest } from '../model/book-property-request';
import { BookPropertyResponse } from '../model/book-property-response';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  http = inject(HttpClient)

  currentPropertyToBook: ReplaySubject<RentalProperty> = new ReplaySubject<RentalProperty>(1);
  currentPropertyToBookIndex: ReplaySubject<number> = new ReplaySubject<number>(1);
  
  bookProperty(request: BookPropertyRequest): Observable<BookPropertyResponse> {
    return this.http.post<BookPropertyResponse>(environment.apiUrl + ROUTE.BOOKPROPERTY, request);
  }
}