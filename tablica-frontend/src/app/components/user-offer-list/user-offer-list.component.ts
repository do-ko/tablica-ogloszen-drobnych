import { Component, OnInit } from '@angular/core';
import { OfferService } from '../../services/offer.service';
import { Offer, OfferStatus } from '../../models/offer.model';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { CommonModule, DatePipe, SlicePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { HeaderComponent } from '../header/header.component';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-user-offer-list',
  templateUrl: './user-offer-list.component.html',
  imports: [
    ReactiveFormsModule,
    SlicePipe,
    DatePipe,
    RouterLink,
    CommonModule,
    HeaderComponent
  ],
  styleUrls: ['./user-offer-list.component.scss']
})
export class UserOfferListComponent implements OnInit {
  offers: Offer[] = [];
  filteredOffers: Offer[] = [];
  searchForm: FormGroup;
  selectedTags: string[] = [];
  tagSuggestions: string[] = [];
  currentUser: User | null = null;
  isLoading = true;
  error: string | null = null;

  constructor(
    private offerService: OfferService,
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.searchForm = this.fb.group({
      searchQuery: [''],
      tagInput: ['']
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();

    if (!this.currentUser) {
      this.router.navigate(['/auth']);
      return;
    }

    this.loadUserOffers();

    this.searchForm.get('searchQuery')?.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(() => this.filterOffers());

    this.searchForm.get('tagInput')?.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(value => {
        if (value) {
          this.offerService.getTagSuggestions(value).subscribe(
            tags => {
              this.tagSuggestions = tags;
            },
            error => {
              console.error('Error occurred while loading tags', error);
              this.tagSuggestions = [];
            }
          );
        } else {
          this.tagSuggestions = [];
        }
      });
  }

  loadUserOffers(): void {
    if (!this.currentUser?.userId) return;

    this.isLoading = true;
    this.error = null;

    this.offerService.getUserOffers(this.currentUser.userId)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe(
        offers => {
          this.offers = offers;
          this.filteredOffers = offers;
        },
        error => {
          console.error('Error occurred while loading offers', error);
          this.error = 'Could not load offers. Please try again later.';
        }
      );
  }

  filterOffers(): void {
    if (!this.currentUser?.userId) return;

    const query = this.searchForm.get('searchQuery')?.value || '';
    this.isLoading = true;

    this.offerService.searchUserOffers(this.currentUser.userId, query, this.selectedTags)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe(
        offers => {
          this.filteredOffers = offers;
        },
        error => {
          console.error('Error while searching offers', error);
          this.error = 'Error occurred while searching offers. Please try again later.';
        }
      );
  }

  addTag(tag: string): void {
    if (!this.selectedTags.includes(tag)) {
      this.selectedTags.push(tag);
      this.searchForm.get('tagInput')?.setValue('');
      this.tagSuggestions = [];
      this.filterOffers();
    }
  }

  removeTag(tag: string): void {
    this.selectedTags = this.selectedTags.filter(t => t !== tag);
    this.filterOffers();
  }

  getStatusClass(status: OfferStatus): string {
    switch (status) {
      case OfferStatus.PUBLISHED:
        return 'badge bg-success';
      case OfferStatus.DRAFT:
        return 'badge bg-warning';
      case OfferStatus.ARCHIVE:
        return 'badge bg-secondary';
      default:
        return 'badge bg-info';
    }
  }

  changeStatus(offerId: string, status: OfferStatus, event: Event): void {
    event.preventDefault();
    event.stopPropagation();

    this.offerService.changeOfferStatus(offerId, status).subscribe(
      updatedOffer => {
        this.offers = this.offers.map(offer =>
          offer.offerId === offerId ? { ...offer, status } : offer
        );
        this.filteredOffers = this.filteredOffers.map(offer =>
          offer.offerId === offerId ? { ...offer, status } : offer
        );
      },
      error => {
        console.error('Error occurred while changing offer status', error);
      }
    );
  }

  retryLoad(): void {
    this.loadUserOffers();
  }

  protected readonly OfferStatus = OfferStatus;
}
