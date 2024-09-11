import { Component, OnInit, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { HeaderComponent } from './components/header/header.component';
import { AuthService } from './services/auth.service';
import { MessagesService } from './services/messages.service';
import { HomeComponent } from './pages/home/home.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    HeaderComponent,
    ToastModule,
    CommonModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  providers: [MessageService]
})
export class AppComponent implements OnInit {
  title = 'frontend';

  authService = inject(AuthService);
  messageService = inject(MessageService)
  messagesService = inject(MessagesService);

  ngOnInit(): void {
    if (localStorage.getItem('token')) {
      this.authService.setToken(localStorage.getItem('token')!);
    }
    this.messagesService.getMessages().subscribe((message) => this.messageService.add(message))
  }
}
