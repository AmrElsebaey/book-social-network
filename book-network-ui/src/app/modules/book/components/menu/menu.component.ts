import { Component, OnInit } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { KeycloakService } from '../../../../services/keycloak/keycloak.service';
import SockJs from 'sockjs-client';
import * as Stomp from 'stompjs';
import { Notification } from './notification';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss',
})
export class MenuComponent implements OnInit {
  tokenHelper: JwtHelperService = new JwtHelperService();
  token: string | undefined;
  name: string | undefined;
  socketClient: any = null;
  private notificationSubscription: any;
  unreadNotificationsCount = 0;
  notifications: Array<Notification> = [];
  constructor(
    private keyCloakService: KeycloakService,
    private toastService: ToastrService
  ) {}
  ngOnInit(): void {
    this.navigationHandler();

    if (this.keyCloakService.keyCloak.tokenParsed?.sub) {
      let ws = new SockJs('http://localhost:8088/api/v1/ws');
      this.socketClient = Stomp.over(ws);
      this.socketClient.connect(
        {
          Authorization: 'Bearer ' + this.keyCloakService.keyCloak.token,
        },
        () => {
          this.notificationSubscription = this.socketClient.subscribe(
            `/user/${this.keyCloakService.keyCloak.tokenParsed?.sub}/notification`,
            (message: any) => {
              const notification: Notification = JSON.parse(message.body);
              if (notification) {
                this.notifications.unshift(notification);
                switch (notification.status) {
                  case 'BORROWED':
                    this.toastService.info(
                      notification.message,
                      notification.bookTitle
                    );
                    break;
                  case 'RETURNED':
                    this.toastService.warning(
                      notification.message,
                      notification.bookTitle
                    );
                    break;
                  case 'RETURNED_APPROVED':
                    this.toastService.success(
                      notification.message,
                      notification.bookTitle
                    );
                    break;
                }
                this.unreadNotificationsCount++;
              }
            }
          );
        }
      );
    }
  }

  private navigationHandler() {
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
    this.keyCloakService.logout();
  }

  get username() {
    return this.keyCloakService.keyCloak.tokenParsed?.['given_name'];
  }
}
