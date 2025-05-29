export interface User {
  userId: string;
  userName: string;
  roles: Set<UserRole>;
  contactData: ContactData;
}

export enum UserRole {
  BUYER,
  SELLER,
}

export interface ContactData {
  phone?: string;
  email?: string;
  showPhone: boolean;
  showEmail: boolean;
}
