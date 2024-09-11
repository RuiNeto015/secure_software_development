import { Component, OnInit, inject } from '@angular/core';
import { RentalPropertyService } from '../../services/rental-property.service';
import { RentalProperty } from '../../model/rental-property';
import { Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  propertyService = inject(RentalPropertyService)
  bookingService = inject(BookingService);
  router: Router = inject(Router);
  properties: RentalProperty[] = [];
  
  ngOnInit(): void {
    this.initData();
  }

  initData() {
    this.propertyService.getAll().subscribe((data) => {
      this.properties = data;
    }); 
  }

  rentProperty(propertyId: any) {
    this.bookingService.currentPropertyToBookIndex.next(propertyId);
    this.bookingService.currentPropertyToBook.next(this.properties[propertyId]);
    this.router.navigateByUrl(`/rent/${propertyId}`);
  }
}
