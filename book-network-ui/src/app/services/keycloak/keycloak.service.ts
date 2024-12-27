import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { UserProfile } from './user-profile';

@Injectable({
  providedIn: 'root',
})
export class KeycloakService {
  private _keyCloak: Keycloak | undefined;
  private _profile: UserProfile | undefined;

  get keyCloak(): Keycloak {
    if (!this._keyCloak) {
      this._keyCloak = new Keycloak({
        url: 'http://localhost:9090',
        realm: 'book-network-social',
        clientId: 'bsn',
      });
    }
    return this._keyCloak;
  }

  get profile(): UserProfile | undefined {
    return this._profile;
  }

  constructor() {}

  async init() {
    const authenticated = await this.keyCloak?.init({
      onLoad: 'login-required',
    });
    if (authenticated) {
      this._profile = (await this.keyCloak?.loadUserProfile()) as UserProfile;
      this._profile.token = this.keyCloak?.token;
    }
  }

  login() {
    return this.keyCloak?.login();
  }

  logout() {
    return this.keyCloak.logout({
      redirectUri: 'http://localhost:4200',
    });
  }
}
