import { Component } from '@angular/core';
import { AuthenticationRequest } from '../../services/models';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../../services/services';
import { Router } from '@angular/router';
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

constructor(
  private router: Router,
  private authSerive: AuthenticationService
){

}

  authReques : AuthenticationRequest={
  email: '',
  password: ''
};
errorMsg : Array<String> = [];

login() : void {
  this.errorMsg=[];
  this.authSerive.authenticate({
    body:this.authReques
  }).subscribe({
      next:(res => this.router.navigate(['books']))
})
}


register(): void {
  this.router.navigate(['register']);
}
}
