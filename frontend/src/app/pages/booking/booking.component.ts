import { Component, OnInit, inject } from '@angular/core';
import { BookingService } from '../../services/booking.service';
import { RentalProperty } from '../../model/rental-property';
import { Interval } from "../../model/intervals";
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BookPropertyRequest } from '../../model/book-property-request';
import { Router } from '@angular/router';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule 
  ],
  templateUrl: './booking.component.html',
  styleUrl: './booking.component.css'
})
export class BookingComponent implements OnInit{

  fb = inject(FormBuilder)
  router: Router = inject(Router);
  bookingService = inject(BookingService);
  
  property: RentalProperty | undefined;
  propertyIndex: number | undefined;
  submitted: boolean = false;

  payment =  {
    "moneyAmount": 200,
    "creditCardNumber": "4111111111111111",
    "cardVerificationCode": "111",
    "expirationDate": "2025-12-30T00:00",
    "email": "propertyowner@mail.com",
    "personName": "Property Owner",
    "createdAt": "2024-10-15T00:00"
  }

  form = this.fb.group({
    accountId: ['', Validators.required],
    propertyId: ['', Validators.required],
    payment: [this.payment, Validators.required],
    from: ['', Validators.required],
    to: ['', Validators.required]
  });

  ngOnInit(): void {
    this.bookingService.currentPropertyToBook.subscribe((property) => {
      this.property = property;
      this.form.controls.propertyId.setValue(this.property!.id);
    });

    this.bookingService.currentPropertyToBookIndex.subscribe((propertyId) => {
      this.propertyIndex = propertyId;
    });

    this.form.controls.accountId.setValue('1a1a1a1a-1a1-1a1a-1a1a-1a1a1a1a1a1a');
    this.form.controls.payment.setValue(this.payment);
  }

  onSubmit() {
    this.submitted = true;

      if (this.form.valid) {
        this.form.setErrors({});
        
        var interval: Interval = {
          from: this.form.controls.from.value!,
          to: this.form.controls.to.value!
        };

        const bookingRequest: BookPropertyRequest = {
          accountId: this.form.controls.accountId.value!,
          propertyId: this.form.controls.propertyId.value!,
          payment: this.form.controls.payment.value!,
          intervalTime: interval
        }

        console.log(bookingRequest);
        this.bookingService.bookProperty(bookingRequest).subscribe((response) => {
          this.router.navigateByUrl(`/home`);
        });
      }
  }
}