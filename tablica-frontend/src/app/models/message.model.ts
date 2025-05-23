export interface MessageThread {
  id: string;
  participants: string[];
  offerId?: string;
  subject: string;
  lastMessage: Message;
  messages: Message[];
}

export interface Message {
  messageId: string;
  fromUserId: string;
  toUserId: string;
  subject: string;
  content: string;
  offerId?: string;
  createdAt: Date;
  isRead: boolean;
  parentMessageId?: string;
}
