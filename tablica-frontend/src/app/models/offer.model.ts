import {ContactInfo} from './user.model';

export interface Offer {
  offerId: string;
  title: string;
  description: string;
  images: string[];
  tags: string[];
  contactInfo: ContactInfo;
  sellerId: string;
  status: OfferStatus;
  createdAt: Date;
  updatedAt: Date;
  previewToken?: string;
}

export enum OfferStatus {
  DRAFT,
  PUBLISHED,
  ARCHIVED
}
