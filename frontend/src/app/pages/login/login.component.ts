import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ROUTE } from '../../enum/routes';
import { LoginRequest } from '../../model/login-request';
import { LoginResponse } from '../../model/login-response';
import { AuthService } from '../../services/auth.service';
import { MessagesService } from '../../services/messages.service';

/**
 *  by owasp: https://owasp.org/www-community/OWASP_Validation_Regex_Repository 
 */
const EMAIL_REGEXP = /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}$/;

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ButtonModule, PasswordModule, InputTextModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  fb = inject(FormBuilder)
  authService = inject(AuthService)
  router = inject(Router)
  messagesService = inject(MessagesService);

  form = this.fb.group({
    email: ['', Validators.required],
    password: ['', Validators.required]
  });

  // timeouts in milliseconds for when login fails
  timeouts: number[] = [30 * 1000, 60 * 1000, 300 * 1000, 900 * 1000, 3600 * 1000];
  timeoutsStrings: string[] = ['30  seconds', '1 minute', '5 minutes', '15 minutes', '1 hour']
  timeoutString: string = '';
  timeout: number = 0;

  // individual failed attempt
  failedAttempts: number = 0;
  // total attempts (number of times the one above was equal to three)
  totalFailedAttempts: number = 0;

  // if the login button should be disabled
  disabled: boolean = false;
  submitted: boolean = false;

  constructor() {
    this.form.valueChanges.subscribe(() => this.submitted = false)
  }

  validateInputs(): Boolean {
    let valid = true;
    if (!EMAIL_REGEXP.test(this.form.controls.email.value!)) {
      this.form.controls.email.setErrors({ email: true })
      valid = false;
    }
    return valid;
  }

  onSubmit() {
    this.submitted = true;
    if (this.validateInputs()) {
      if (this.form.valid) {
        this.form.setErrors({});
        const credentials: LoginRequest = {
          email: this.form.controls.email.value!,
          password: this.form.controls.password.value!
        }
        this.authService.login(credentials).subscribe({
          next: (response: LoginResponse) => this.handleLoginResponse(response),
          error: (error: HttpErrorResponse | HttpResponse<LoginResponse>) => this.handleError(error)
        })
      }
    }
  }

  handleLoginResponse(response: LoginResponse) {
    if (response.token) {
      this.router.navigateByUrl('/home');
    } else {
      this.failedAttempts += 1;
      if (this.failedAttempts === 3) {
        this.disabled = true;
        if (this.totalFailedAttempts < this.timeouts.length) {
          this.timeout = this.timeouts[this.totalFailedAttempts]
          this.timeoutString = this.timeoutsStrings[this.totalFailedAttempts]
        } else {
          this.timeout = this.timeouts[this.timeouts.length - 1]
          this.timeoutString = this.timeoutsStrings[this.timeoutsStrings.length - 1]
        }
        setTimeout(() => {
          this.form.setErrors({ tooManyFailures: true })
          this.disabled = false;
        }, this.timeouts[this.totalFailedAttempts])
        this.failedAttempts = 0;
        this.totalFailedAttempts++;
      } else {
        this.form.setErrors({ invalidCredentials: true })
      }
    }
  }

  handleError(error: any) {
    if (error.name) {
      this.messagesService.error('There was a problem sending the request', 'Oopsie Daisy')
    } else {
      this.messagesService.error('Take a breather', 'Rate Limit Exceeded')
    }
  }
}
