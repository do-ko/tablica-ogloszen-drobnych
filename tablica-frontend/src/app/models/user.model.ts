export interface User {
  userId: string;
  userName: string;
  email: string;
  phone?: string;
  roles: Set<UserRole>;
  contactInfo: ContactInfo;
}

export enum UserRole {
  BUYER,
  SELLER,
  BOTH
}

export interface ContactInfo {
  phone?: string;
  email?: string;
  showPhone: boolean;
  showEmail: boolean;
}
