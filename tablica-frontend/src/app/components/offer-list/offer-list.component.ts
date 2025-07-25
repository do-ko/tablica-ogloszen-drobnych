import { Component, OnInit } from '@angular/core';
import { OfferService } from '../../services/offer.service';
import { Offer, OfferStatus } from '../../models/offer.model';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import {CommonModule, DatePipe, SlicePipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import { HeaderComponent } from '../header/header.component';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-offer-list',
  templateUrl: './offer-list.component.html',
  imports: [
    ReactiveFormsModule,
    SlicePipe,
    DatePipe,
    RouterLink,
    CommonModule,
    HeaderComponent
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
  error: string | null = null;

  pageNumber = 0;
  pageSize = 12;
  totalElements = 0;
  totalPages = 0;

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
          this.offerService.getTagSuggestions(value).subscribe(
            tags => {
              this.tagSuggestions = tags;
            },
            error => {
              console.error('Error fetching tag suggestions', error);
              this.tagSuggestions = [];
            }
          );
        } else {
          this.tagSuggestions = [];
        }
      });
  }

  loadOffers(): void {
    this.isLoading = true;
    this.error = null;

    this.offerService.getPublishedOffers(this.pageNumber, this.pageSize)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe(
        response => {
          this.offers = response.content;
          this.filteredOffers = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
        },
        error => {
          console.error('Error loading offers', error);
          this.error = 'Error occured while loading offers. Please try again later.';
        }
      );
  }

  filterOffers(): void {
    const query = this.searchForm.get('searchQuery')?.value || '';
    this.isLoading = true;

    this.offerService.searchOffers(query, this.selectedTags)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe(
        offers => {
          this.filteredOffers = offers
        },
        // response => {
        //   this.filteredOffers = response.content;
        //   this.totalElements = response.totalElements;
        //   this.totalPages = response.totalPages;
        // },
        error => {
          console.error('Error searching offers', error);
          this.error = 'Error occured while searching offers. Please try again later.';
        }
      );
  }

  changePage(page: number): void {
    this.pageNumber = page;
    if (this.searchForm.get('searchQuery')?.value || this.selectedTags.length > 0) {
      this.filterOffers();
    } else {
      this.loadOffers();
    }
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

  retryLoad(): void {
    this.loadOffers();
  }
}
