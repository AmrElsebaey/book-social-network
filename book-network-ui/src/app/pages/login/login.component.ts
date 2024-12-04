import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

// Services
import { AuthenticationService } from '../../services/services';
import { TokenService } from '../../services/token/token.service';

// Models
import {
  AuthenticationRequest,
  AuthenticationResponse,
} from '../../services/models';

// Login status type
type LoginStatus = 'idle' | 'loading' | 'success' | 'error';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit {
  // Authentication request model
  authRequest: AuthenticationRequest = {
    email: '',
    password: '',
  };

  // Error management
  errorMsg: string[] = [];

  // Login status for UI feedback
  loginStatus: LoginStatus = 'idle';

  // Password visibility toggle
  showPassword = false;

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private tokenService: TokenService
  ) {}

  ngOnInit(): void {
    // Check if user is already logged in
    if (this.tokenService.token) {
      this.router.navigate(['books']);
    }
  }

  // Toggle password visibility
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  // Clear error messages
  clearErrors(): void {
    this.errorMsg = [];
    this.loginStatus = 'idle';
  }

  // Login method with enhanced error handling
  login(loginForm: NgForm): void {
    // Check form validity
    if (loginForm.invalid) {
      this.errorMsg = ['Please fill in all required fields'];
      this.loginStatus = 'error';
      return;
    }

    // Reset previous state
    this.clearErrors();
    this.loginStatus = 'loading';

    // Validate input before sending request
    if (this.validateInput()) {
      this.authService.authenticate({ body: this.authRequest }).subscribe({
        next: (res: AuthenticationResponse): void => {
          // Null/undefined check for token
          if (!res.token) {
            this.handleLoginError('Invalid authentication response');
            return;
          }

          // Store token
          this.tokenService.token = res.token as string;

          // Set success status
          this.loginStatus = 'success';

          // Delayed navigation to show success state
          setTimeout(() => {
            this.router.navigate(['books']);
          }, 1500);
        },
        error: (err) => {
          this.handleLoginError(err);
        },
      });
    }
  }

  // Centralized error handling method
  private handleLoginError(err: any): void {
    // Set error status
    this.loginStatus = 'error';

    // Handle different error scenarios
    if (err.error?.validationErrors) {
      // Validation errors
      this.errorMsg = err.error.validationErrors;
    } else if (err.error?.error) {
      // Server-side error message
      this.errorMsg = [err.error.error];
    } else if (typeof err === 'string') {
      // Direct string error
      this.errorMsg = [err];
    } else {
      // Generic error message
      this.errorMsg = ['Login failed. Please try again.'];
    }

    // Log error for debugging
    console.error('Login Error:', err);
  }

  // Input validation method
  private validateInput(): boolean {
    this.errorMsg = [];

    // Email validation
    if (!this.authRequest.email) {
      this.errorMsg.push('Email is required');
    } else if (!this.isValidEmail(this.authRequest.email)) {
      this.errorMsg.push('Invalid email format');
    }

    // Password validation
    if (!this.authRequest.password) {
      this.errorMsg.push('Password is required');
    } else if (this.authRequest.password.length < 6) {
      this.errorMsg.push('Password must be at least 6 characters');
    }

    return this.errorMsg.length === 0;
  }

  // Email validation helper method
  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  // Navigation to registration page
  register(): void {
    this.router.navigate(['register']);
  }

  // Forgot password navigation
  forgotPassword(): void {
    this.router.navigate(['forgot-password']);
  }
}
