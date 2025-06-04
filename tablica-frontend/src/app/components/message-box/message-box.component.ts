import {Component, OnInit, OnDestroy} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Message, MessageThread } from '../../models/message.model';
import { MessageService } from '../../services/message.service';
import { WebSocketService } from '../../services/websocket.service';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { Subscription } from 'rxjs';
import { MessageBoxService } from '../../services/message-box.service';

@Component({
  selector: 'app-message-box',
  templateUrl: './message-box.component.html',
  styleUrls: ['./message-box.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class MessageBoxComponent implements OnInit, OnDestroy {
  isOpen = false;
  threads: MessageThread[] = [];
  currentThread: MessageThread | null = null;
  currentThreadId: string | null = null;
  newMessage = '';
  currentUser: User | null = null;
  isLoading = false;
  private subscription = new Subscription();

  constructor(
    private messageService: MessageService,
    private webSocketService: WebSocketService,
    private messageBoxService: MessageBoxService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.webSocketService.connect();

    this.subscription.add(
      this.webSocketService.getThreadUpdates().subscribe(threadId => {
        if (threadId === 'all') {
          this.loadThreads();
        } else if (threadId === this.currentThreadId) {
          this.loadCurrentThread();
        }
      })
    );

    this.subscription.add(
      this.messageBoxService.messageBoxAction$.subscribe(action => {
        if (action.action === 'newThread') {
          this.createNewThread(
            action.data.receiverId,
            action.data.subject,
            action.data.offerId
          );
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.webSocketService.disconnect();
  }

  toggleMessageBox(): void {
    this.isOpen = !this.isOpen;
    if (this.isOpen && this.threads.length === 0) {
      this.loadThreads();
    }
  }

  loadThreads(): void {
    this.isLoading = true;
    this.messageService.getThreads().subscribe({
      next: (threads) => {
        this.threads = threads;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading message threads.', error);
        this.isLoading = false;
      }
    });
  }

  openThread(threadId: string): void {
    this.currentThreadId = threadId;
    this.loadCurrentThread();
  }

  loadCurrentThread(): void {
    if (!this.currentThreadId) return;

    this.isLoading = true;
    this.messageService.getThreadById(this.currentThreadId).subscribe({
      next: (thread) => {
        this.currentThread = thread;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading message thread.', error);
        this.isLoading = false;
      }
    });
  }

  sendMessage(): void {
    if (!this.newMessage.trim()) return;

    if (this.currentThread && this.currentThread.id) {
      this.messageService.sendMessage(this.newMessage, this.currentThread.id).subscribe({
        next: (message) => {
          if (this.currentThread) {
            this.currentThread.messages.push(message);
            this.newMessage = '';
          }
        },
        error: (error) => {
          console.error('Error while sending message.', error);
        }
      });
    } else if (this.currentThread && !this.currentThread.id && this.currentThread.participants.length > 1) {
      const receiverId = this.currentThread.participants.find(id => id !== this.currentUser?.userId);
      if (receiverId) {
        this.messageService.createThread(
          receiverId,
          this.currentThread.subject,
          this.newMessage,
          this.currentThread.offerId
        ).subscribe({
          next: (thread) => {
            this.currentThread = thread;
            this.currentThreadId = thread.id;
            this.newMessage = '';
          },
          error: (error) => {
            console.error('Error while creating thread.', error);
          }
        });
      }
    }
  }

  backToThreads(): void {
    this.currentThread = null;
    this.currentThreadId = null;
  }

  createNewThread(receiverId: string, subject: string, offerId?: string): void {
    this.isOpen = true;

    this.currentThread = {
      id: '',
      participants: [this.currentUser?.userId || '', receiverId],
      subject: subject,
      messages: [],
      lastMessage: {} as Message,
      createdAt: new Date(),
      updatedAt: new Date(),
      offerId: offerId
    };

    this.currentThreadId = null;
  }
}
