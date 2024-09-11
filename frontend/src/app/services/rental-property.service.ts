import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { ROUTE } from '../enum/routes';
import { Observable } from 'rxjs';
import { RentalProperty } from '../model/rental-property';

@Injectable({
  providedIn: 'root'
})
export class RentalPropertyService {

  http = inject(HttpClient)

  getAll(): Observable<RentalProperty[]> {
    return this.http.get<RentalProperty[]>(environment.apiUrl + ROUTE.RENTALPROPERTIES);
  }

  getUserRentalProperties(userId: number): Observable<RentalProperty[]> {
    return this.http.get<RentalProperty[]>(environment.apiUrl + ROUTE.RENTALPROPERTIESBYUSER + '/' + userId);
  }
}