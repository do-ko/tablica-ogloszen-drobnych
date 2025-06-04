export interface Message {
  id: string;
  sender: string;
  content: string;
  createdAt: Date;
  threadId: string;
  isRead: boolean;
}

export interface MessageThread {
  id: string;
  subject: string;
  participants: string[];
  messages: Message[];
  lastMessage: Message;
  createdAt: Date;
  updatedAt: Date;
  offerId?: string;
}
