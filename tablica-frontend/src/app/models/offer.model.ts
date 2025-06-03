import {ContactData} from './user.model';

export interface OfferImage {
  id: string;
  path: string;
}

export interface Offer {
  offerId: string;
  title: string;
  description: string;
  images: OfferImage[];
  tags: string[];
  contactData: ContactData;
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
