import {Component, OnInit} from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {CommonModule, Location} from '@angular/common';
import { OfferService } from '../../services/offer.service';
import { AuthService } from '../../services/auth.service';
import { Offer, OfferStatus } from '../../models/offer.model';
import { HeaderComponent } from '../header/header.component';
import { User } from '../../models/user.model';
import {environment} from '../../enviroment';
import {MessageBoxService} from '../../services/message-box.service';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-offer-detail',
  templateUrl: './offer-detail.component.html',
  styleUrls: ['./offer-detail.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent
  ]
})
export class OfferDetailComponent implements OnInit {
  offer: Offer | null = null;
  isLoading = true;
  errorMessage = '';
  currentUser: User | null = null;
  isOwner = false;
  currentImageIndex = 0;
  imageUrls: string[] = [];
  sellerUserName: string | null = null;

  constructor(
    private offerService: OfferService,
    private authService: AuthService,
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router,
    private messageBoxService: MessageBoxService,
    private location: Location
) {
    this.currentUser = this.authService.getCurrentUser();
  }

  ngOnInit(): void {
    const offerId = this.route.snapshot.paramMap.get('id');
    if (!offerId) {
      this.router.navigate(['/offers']);
      return;
    }

    this.loadOffer(offerId);
  }

  loadOffer(offerId: string): void {
    this.isLoading = true;
    this.offerService.getOfferById(offerId).subscribe({
      next: (offer) => {
        this.offer = offer;
        this.isOwner = this.currentUser?.userId === offer.sellerId;
        this.preloadImages(offer);
        this.isLoading = false;
        this.userService.getUserNameById(offer.sellerId).subscribe({
          next: (response) => {
            this.sellerUserName = response.username;
          },
          error: (error) => {
            console.error('Error fetching seller username', error);
            this.sellerUserName = "Unknown Seller";
          }
        });
      },
      error: (error) => {
        console.error('Error loading offer', error);
        this.errorMessage = 'Could not load the offer. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  preloadImages(offer: Offer): void {
    this.imageUrls = [];

    if (offer.images && offer.images.length > 0) {
      offer.images.forEach(image => {
        const fullUrl = `${environment.imagesUrl}/${image.path}`;
        this.imageUrls.push(fullUrl);
      });
    }
  }

  nextImage(): void {
    if (this.offer && this.offer.images.length > 0) {
      this.currentImageIndex = (this.currentImageIndex + 1) % this.offer.images.length;
    }
  }

  prevImage(): void {
    if (this.offer && this.offer.images.length > 0) {
      this.currentImageIndex = (this.currentImageIndex - 1 + this.offer.images.length) % this.offer.images.length;
    }
  }

  editOffer(): void {
    if (this.offer) {
      this.router.navigate(['/offers/edit', this.offer.offerId]);
    }
  }

  archiveOffer(): void {
    if (this.offer) {
      if (confirm('Are you sure to archive this offer?')) {
        const updatedOffer = { ...this.offer, status: OfferStatus.ARCHIVE };
        this.offerService.updateOffer(this.offer.offerId, updatedOffer).subscribe({
          next: () => {
            this.router.navigate(['/my-offers']);
          },
          error: (error) => {
            console.error('Error archiving offer', error);
            this.errorMessage = 'Could not archive the offer. Please try again later.';
          }
        });
      }
    }
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('pl-PL');
  }

  hasContactEmail(): boolean {
    return !!this.offer?.contactData?.email && this.offer.contactData.email.trim() !== '';
  }

  hasContactPhone(): boolean {
    return !!this.offer?.contactData?.phone && this.offer.contactData.phone.trim() !== '';
  }

  hasAnyContactInfo(): boolean {
    return this.hasContactEmail() || this.hasContactPhone();
  }

  goBack(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/offers']);
    }
  }

  getImageUrl(index: number): string {
    if (index >= 0 && index < this.imageUrls.length) {
      return this.imageUrls[index];
    }
    return '';
  }

  contactSeller(): void {
    if (this.offer && this.currentUser) {
      this.messageBoxService.triggerNewThread(
        this.offer.sellerId,
        `Regarding: ${this.offer.title}`,
        this.offer.offerId
      );
    }
  }
}
