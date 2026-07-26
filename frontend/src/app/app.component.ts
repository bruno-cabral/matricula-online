import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { NotificationService } from './services/notification.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <nav class="sidebar">
      <div class="logo">Matrícula Online</div>
      <a routerLink="/alunos" routerLinkActive="active">Alunos</a>
      <a routerLink="/cursos" routerLinkActive="active">Cursos</a>
      <a routerLink="/disciplinas" routerLinkActive="active">Disciplinas</a>
      <a routerLink="/turmas" routerLinkActive="active">Turmas</a>
      <a routerLink="/matriculas" routerLinkActive="active">Matrículas</a>
    </nav>

    <main class="main-content">
      <div class="container">
        <router-outlet />
      </div>
    </main>

    @if (notification.notification(); as notif) {
      <div class="notification" [class]="notif.type" (click)="notification.dismiss()">
        {{ notif.message }}
      </div>
    }
  `
})
export class AppComponent {
  constructor(public notification: NotificationService) {}
}
