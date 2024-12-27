import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { AuthenticationService } from '../../services/services';

import {
  AuthenticationRequest,
  AuthenticationResponse,
} from '../../services/models';
import { KeycloakService } from '../../services/keycloak/keycloak.service';

type LoginStatus = 'idle' | 'loading' | 'success' | 'error';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit {
  constructor(
    private keyCloakService: KeycloakService
  ) {}

  async ngOnInit(): Promise<void> {
    await this.keyCloakService.init();
    await this.keyCloakService.login();
  }
}
