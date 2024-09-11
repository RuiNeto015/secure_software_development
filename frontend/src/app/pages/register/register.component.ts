import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ROUTE } from '../../enum/routes';
import { RegisterRequest } from '../../model/register-request';
import { RegisterResponse } from '../../model/register-response';
import { AuthService } from '../../services/auth.service';
import { MessagesService } from '../../services/messages.service';

/**
 *  by owasp: https://owasp.org/www-community/OWASP_Validation_Regex_Repository 
 */
const EMAIL_REGEXP = /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}$/;

interface PasswordRequirement {
  requirement: string,
  fulfilled: boolean
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {



  authService = inject(AuthService);
  router = inject(Router);
  messagesService = inject(MessagesService);

  form = new FormGroup({
    name: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
    confirmPassword: new FormControl('', [Validators.required]),
    role: new FormControl('', Validators.required)
  })

  tosForm = new FormGroup({
    tos: new FormControl(null, [Validators.required])
  })

  passwordRequirements: PasswordRequirement[];

  constructor() {
    this.form.controls.password.valueChanges.subscribe((password) => this.checkPasswordRequirements(password!))
    this.form.valueChanges.subscribe(() => this.submitted = false)
    this.passwordRequirements = [
      { requirement: 'At least 12 characters long', fulfilled: false },
      { requirement: 'Less than 128 characters long', fulfilled: false },
      { requirement: 'Contains at least one lowercase letter', fulfilled: false },
      { requirement: 'Contains at least one uppercase letter', fulfilled: false },
      { requirement: 'Contains at least one digit', fulfilled: false },
      { requirement: 'Contains at least one special character', fulfilled: false },
      { requirement: 'No more than 2 equal characters in a row', fulfilled: false }
    ];
  }

  submitted: boolean = false;
  showOverlay: boolean = false;

  checkPasswordRequirements(password: string): void {
    this.passwordRequirements[0].fulfilled = password.length >= 12;
    this.passwordRequirements[1].fulfilled = password.length < 128;
    this.passwordRequirements[2].fulfilled = /[a-z]/.test(password);
    this.passwordRequirements[3].fulfilled = /[A-Z]/.test(password);
    this.passwordRequirements[4].fulfilled = /\d/.test(password);
    this.passwordRequirements[5].fulfilled = /[!@#$%^&*]/.test(password);
    this.passwordRequirements[6].fulfilled = !/(.)\1{2,}/.test(password);
  }

  showPasswordRequirements(): void {
    this.showOverlay = true;
  }

  hidePasswordRequirements(): void {
    this.showOverlay = false;
  }

  validateInputs(): Boolean {
    let valid = true;
    if (this.form.controls.password.value != this.form.controls.confirmPassword.value) {
      this.form.setErrors({ matchingPasswords: true })
      valid = false;
    }
    if (!EMAIL_REGEXP.test(this.form.controls.email.value!)) {
      this.form.controls.email.setErrors({ email: true })
      valid = false;
    }
    return valid;
  }

  onSubmit(): void {
    console.log(this.form)
    this.submitted = true;
    if (this.validateInputs()) {
      if (this.tosForm.controls.tos.value && this.form.valid) {
        const body: RegisterRequest = {
          name: this.form.controls.name.value!,
          email: this.form.controls.email.value!,
          password: this.form.controls.password.value!,
          role: this.form.controls.role.value!
        }
        this.authService.register(body)
          .subscribe({
            next: (data: RegisterResponse) => this.handleRegisterResponse(data),
            error: (err) => this.handleError(err)
          });
      }
    }
  }

  handleRegisterResponse(response: RegisterResponse): void {
    console.debug(response)
    // emit some toastr event or something herer
    this.messagesService.success('Registered with success! You can now login!', 'Registered')
    this.router.navigate([ROUTE.LOGIN]);
  }

  handleError(error: any): void {
    if (error.name) {
      this.messagesService.error('There was a problem sending the request', 'Oopsie Daisy')
    } else {
      this.messagesService.error('Take a breather', 'Rate Limit Exceeded')
    }
  }
}
