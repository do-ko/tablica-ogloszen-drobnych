import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message, MessageThread } from '../models/message.model';
import {environment} from '../enviroment';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private apiUrl = `${environment.apiUrl}/messages`;

  constructor(private http: HttpClient) {}

  getThreads(): Observable<MessageThread[]> {
    return this.http.get<MessageThread[]>(`${this.apiUrl}/threads`);
  }

  getThreadById(threadId: string): Observable<MessageThread> {
    return this.http.get<MessageThread>(`${this.apiUrl}/threads/${threadId}`);
  }

  createThread(receiverId: string, subject: string, content: string, offerId?: string): Observable<MessageThread> {
    return this.http.post<MessageThread>(`${this.apiUrl}/threads`, {
      receiverId,
      subject,
      content,
      offerId
    });
  }

  sendMessage(content: string, threadId: string): Observable<Message> {
    return this.http.post<Message>(`${this.apiUrl}/send`, {
      content,
      threadId
    });
  }
}
