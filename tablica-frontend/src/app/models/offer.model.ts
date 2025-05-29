import {ContactData} from './user.model';

export interface Offer {
  offerId: string;
  title: string;
  description: string;
  images: string[];
  tags: string[];
  contactInfo: ContactData;
  sellerId: string;
  status: OfferStatus;
  createdAt: Date;
  updatedAt: Date;
}

export enum OfferStatus {
  DRAFT = 'WORK_IN_PROGRESS',
  PUBLISHED = 'PUBLISHED',
  ARCHIVE = 'ARCHIVE'
}
