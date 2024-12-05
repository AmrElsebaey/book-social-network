import { Component, OnInit } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { TokenService } from '../../../../services/token/token.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss',
})
export class MenuComponent implements OnInit {
  tokenHelper: JwtHelperService = new JwtHelperService();
  token: string | undefined;
  name: string | undefined;

  constructor(private tokenService: TokenService) {
    this.token = tokenService.token as string;
    if (this.token) {
      const fullName = this.tokenHelper.decodeToken(this.token).fullName;
      this.name = fullName.split(' ')[0];
    }
  }
  ngOnInit(): void {
    const linkColor = document.querySelectorAll('.nav-link');
    linkColor.forEach((link) => {
      if (window.location.href.endsWith(link.getAttribute('href') || '')) {
        link.classList.add('active');
      }
      link.addEventListener('click', () => {
        linkColor.forEach((l) => l.classList.remove('active'));
        link.classList.add('active');
      });
    });
  }

  logout() {
    localStorage.removeItem('token');
    window.location.reload;
  }
}
