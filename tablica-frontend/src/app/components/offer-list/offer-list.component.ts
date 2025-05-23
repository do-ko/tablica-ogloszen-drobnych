import { Component, OnInit } from '@angular/core';
import { OfferService } from '../../services/offer.service';
import { Offer, OfferStatus } from '../../models/offer.model';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import {CommonModule, DatePipe, SlicePipe} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-offer-list',
  templateUrl: './offer-list.component.html',
  imports: [
    ReactiveFormsModule,
    SlicePipe,
    DatePipe,
    RouterLink,
    CommonModule
  ],
  styleUrls: ['./offer-list.component.scss']
})
export class OfferListComponent implements OnInit {
  offers: Offer[] = [];
  filteredOffers: Offer[] = [];
  searchForm: FormGroup;
  selectedTags: string[] = [];
  tagSuggestions: string[] = [];
  currentUser: User | null = null;
  isLoading = true;

  constructor(
    private offerService: OfferService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.searchForm = this.fb.group({
      searchQuery: [''],
      tagInput: ['']
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadOffers();

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
          this.offerService.getTagSuggestions(value).subscribe(tags => {
            this.tagSuggestions = tags;
          });
        } else {
          this.tagSuggestions = [];
        }
      });
  }

  loadOffers(): void {
    this.isLoading = true;
    this.offerService.getPublishedOffers().subscribe(
      offers => {
        this.offers = offers;
        this.filteredOffers = offers;
        this.isLoading = false;
      },
      error => {
        console.error('Error loading offers', error);
        this.isLoading = false;
      }
    );
  }

  filterOffers(): void {
    const query = this.searchForm.get('searchQuery')?.value || '';
    this.offerService.searchOffers(query, this.selectedTags).subscribe(
      offers => {
        this.filteredOffers = offers;
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
      case OfferStatus.ARCHIVED:
        return 'badge bg-secondary';
      default:
        return 'badge bg-info';
    }
  }
}
