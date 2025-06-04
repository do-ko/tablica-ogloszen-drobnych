import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessageBoxService {
  private messageBoxActionSubject = new Subject<{action: string, data: any}>();

  messageBoxAction$ = this.messageBoxActionSubject.asObservable();

  triggerNewThread(receiverId: string, subject: string, offerId?: string) {
    this.messageBoxActionSubject.next({
      action: 'newThread',
      data: { receiverId, subject, offerId }
    });
  }
}
