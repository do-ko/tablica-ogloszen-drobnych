import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { AuthService } from './auth.service';
import { environment } from '../enviroment';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client | null = null;
  private threadUpdatesSubject = new Subject<string>();

  constructor(private authService: AuthService) {}

  connect(): void {
    if (this.client) {
      return;
    }

    const token = this.authService.getToken();
    if (!token) {
      console.error('Cannot connect to WebSocket: No authentication token found');
      return;
    }

    this.client = new Client({
      webSocketFactory: () => new SockJS(`${environment.apiUrl}/ws`),
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    this.client.onConnect = () => {
      console.log('Connected with WebSocket');

      this.client?.subscribe('/user/queue/thread-updates', (message) => {
        const threadId = message.body;
        this.threadUpdatesSubject.next(threadId);
      });
    };

    this.client.onWebSocketError = (error) => {
      console.error('WebSocket error:', error);
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP error:', frame);
    };

    this.client.onDisconnect = () => {
        console.log('WebSocket disconnected');
    };

    this.client.activate();
  }

  disconnect(): void {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
  }

  getThreadUpdates(): Observable<string> {
    return this.threadUpdatesSubject.asObservable();
  }
}
