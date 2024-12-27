import { Token } from '@angular/compiler';
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { KeycloakService } from '../keycloak/keycloak.service';

export const authGuard: CanActivateFn = () => {
 const keyCloakService = inject(KeycloakService);
 const router = inject(Router)
 if(keyCloakService.keyCloak.isTokenExpired()) {
   router.navigate(['login'])
   return false;
 }
 return true
};
