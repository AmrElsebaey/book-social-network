import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

// Services
import { AuthenticationService } from '../../services/services';

// Models
import { RegistrationRequest } from '../../services/models';

// Types for registration status
type RegistrationStatus = 'idle' | 'loading' | 'success' | 'error';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent implements OnInit {
  // Registration request model
  registerRequest: RegistrationRequest = {
    email: '',
    firstName: '',
    lastName: '',
    password: '',
  };

  // Error management
  errorMsg: string[] = [];

  // Password visibility toggle
  showPassword = false;

  // Registration status for UI feedback
  registrationStatus: RegistrationStatus = 'idle';

  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Any initialization logic
  }

  // Toggle password visibility
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  // Clear error messages
  clearErrors(): void {
    this.errorMsg = [];
    this.registrationStatus = 'idle';
  }

  // Registration method with enhanced error handling
  register(): void {
    // Reset previous state
    this.clearErrors();
    this.registrationStatus = 'loading';

    // Validate input before sending request
    if (this.validateInput()) {
      this.authService.register({ body: this.registerRequest })
        .subscribe({
          next: () => {
            // Success handling
            this.registrationStatus = 'success';

            // Delayed navigation to prevent immediate redirect
            setTimeout(() => {
              this.router.navigate(['activate-account']);
            }, 1500);
          },
          error: (err) => {
            // Error handling
            this.registrationStatus = 'error';

            // Check if validation errors exist
            if (err.error?.validationErrors) {
              this.errorMsg = err.error.validationErrors;
            } else {
              // Generic error message
              this.errorMsg = ['Registration failed. Please try again.'];
            }
          }
        });
    }
  }

  // Input validation method
  private validateInput(): boolean {
    this.errorMsg = [];

    // Email validation
    if (!this.registerRequest.email) {
      this.errorMsg.push('Email is required');
    } else if (!this.isValidEmail(this.registerRequest.email)) {
      this.errorMsg.push('Invalid email format');
    }

    // First name validation
    if (!this.registerRequest.firstName) {
      this.errorMsg.push('First name is required');
    } else if (this.registerRequest.firstName.length < 2) {
      this.errorMsg.push('First name must be at least 2 characters');
    }

    // Last name validation
    if (!this.registerRequest.lastName) {
      this.errorMsg.push('Last name is required');
    } else if (this.registerRequest.lastName.length < 2) {
      this.errorMsg.push('Last name must be at least 2 characters');
    }

    // Password validation
    if (!this.registerRequest.password) {
      this.errorMsg.push('Password is required');
    } else if (this.registerRequest.password.length < 8) {
      this.errorMsg.push('Password must be at least 8 characters');
    }

    return this.errorMsg.length === 0;
  }

  // Email validation helper method
  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  // Navigation to login page
  login(): void {
    this.router.navigate(['login']);
  }
}
