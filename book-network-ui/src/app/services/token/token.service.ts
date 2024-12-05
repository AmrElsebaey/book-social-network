import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  isTokenNotValid() {
    return !this.isTokenValid();
  }
  isTokenValid() {
    const token = this.token;
    if (!token) {
      return false;
    }
    const tokenHelper: JwtHelperService = new JwtHelperService();
    const isTokenExpired: boolean = tokenHelper.isTokenExpired(token);
    if (isTokenExpired) {
      localStorage.removeItem('token');
      return false;
    }
    return true;
  }

  set token(token: string) {
    localStorage.setItem('token', token);
  }

  get token(): string {
    return localStorage.getItem('token') as string;
  }
}
