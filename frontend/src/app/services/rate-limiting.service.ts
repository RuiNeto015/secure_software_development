import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RateLimitingService {
  // number of requests allowed per minute
  private capacity = 60;
  private refillRate = 60000;

  //constructor() { }

  tryConsume(): boolean {
    if (this.capacity > 0) {
      this.capacity--;
      setTimeout(() => {
        this.refill();
      }, this.refillRate)
      return true;
    }
    return false;
  }

  refill(): void {
    this.capacity++;
  }

  // Only to be used in testing 
  setCapacity(capacity: number): void {
    this.capacity = capacity
  }
}
