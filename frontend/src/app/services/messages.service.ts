import { Injectable } from '@angular/core';
import { Message } from 'primeng/api';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessagesService {

  messages: Subject<Message> = new Subject();

  getMessages(): Subject<Message> {
    return this.messages
  }

  success(content: string, title: string) {
    this.messages.next({ severity: 'success', summary: title, detail: content })
  }

  error(content: string, title: string) {
    this.messages.next({ severity: 'danger', summary: title, detail: content })
  }

  info(content: string, title: string) {
    this.messages.next({ severity: 'info', summary: title, detail: content })
  }

  warn(content: string, title: string) {
    this.messages.next({ severity: 'warn', summary: title, detail: content })
  }

  contrast(content: string, title: string) {
    this.messages.next({ severity: 'contrast', summary: title, detail: content })
  }

}
