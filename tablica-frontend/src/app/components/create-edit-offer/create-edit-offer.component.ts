import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { OfferService } from '../../services/offer.service';
import { AuthService } from '../../services/auth.service';
import { Offer, OfferStatus } from '../../models/offer.model';
import { HeaderComponent } from '../header/header.component';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-create-edit-offer',
  templateUrl: './create-edit-offer.component.html',
  styleUrls: ['./create-edit-offer.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HeaderComponent
  ]
})
export class CreateEditOfferComponent implements OnInit {
  offerForm: FormGroup;
  isEditMode = false;
  existingOffer: Offer | null = null;
  isLoading = false;
  errorMessage = '';
  tagInput = '';
  selectedTags: string[] = [];
  tagSuggestions: string[] = [];
  currentUser: User | null = null;

  constructor(
    private fb: FormBuilder,
    private offerService: OfferService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.currentUser = this.authService.getCurrentUser();

    this.offerForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      tagInput: [''],
      contactEmail: [this.currentUser?.contactData.email],
      contactPhone: [this.currentUser?.contactData.phone],
      showEmail: [false],
      showPhone: [false]
    });
  }

  ngOnInit(): void {
    const offerId = this.route.snapshot.paramMap.get('id');
    if (offerId) {
      this.isEditMode = true;
      this.isLoading = true;

      this.offerService.getOfferById(offerId).subscribe({
        next: (offer) => {
          if (offer.status === OfferStatus.ARCHIVE) {
            this.router.navigate(['/my-offers']);
            return;
          }

          if (offer.sellerId !== this.currentUser?.userId) {
            this.router.navigate(['/my-offers']);
            return;
          }

          this.existingOffer = offer;
          this.selectedTags = [...offer.tags];

          this.offerForm.patchValue({
            title: offer.title,
            description: offer.description,
          });

          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading offer', error);
          this.router.navigate(['/my-offers']);
          this.isLoading = false;
        }
      });
    }

    this.offerForm.get('tagInput')?.valueChanges.subscribe(value => {
      if (value) {
        this.offerService.getTagSuggestions(value).subscribe(tags => {
          this.tagSuggestions = tags.filter(tag => !this.selectedTags.includes(tag));
        });
      } else {
        this.tagSuggestions = [];
      }
    });
  }

  addTag(tag: string): void {
    if (tag && !this.selectedTags.includes(tag)) {
      this.selectedTags.push(tag);
      this.offerForm.get('tagInput')?.setValue('');
      this.tagSuggestions = [];
    }
  }

  removeTag(tag: string): void {
    this.selectedTags = this.selectedTags.filter(t => t !== tag);
  }

  saveAsDraft(): void {
    this.saveOffer(OfferStatus.DRAFT);
  }

  publish(): void {
    this.saveOffer(OfferStatus.PUBLISHED);
  }

  private saveOffer(status: OfferStatus): void {
    if (this.offerForm.invalid) {
      this.offerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    const formValue = this.offerForm.value;

    const offerData = {
      title: formValue.title,
      description: formValue.description,
      tags: this.selectedTags,
      contactInfo: {
        email: formValue.contactEmail,
        phone: formValue.contactPhone,
        showEmail: formValue.showEmail,
        showPhone: formValue.showPhone
      },
      images: this.existingOffer?.images || [],
      sellerId: this.currentUser?.userId || '',
      status: status
    };

    if (this.isEditMode && this.existingOffer) {
      this.offerService.updateOffer(this.existingOffer.offerId, offerData).subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/my-offers']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = 'Error occurred while updating the offer.';
          console.error('Error updating offer', error);
        }
      });
    } else {
      this.offerService.createOffer(offerData as any).subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/my-offers']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = 'Error occurred while creating the offer.';
          console.error('Error creating offer', error);
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/my-offers']);
  }

  get canPublish(): boolean {
    return true;
  }

  get canSaveAsDraft(): boolean {
    return !this.existingOffer || this.existingOffer.status !== OfferStatus.PUBLISHED;
  }

  get isPublished(): boolean {
    return this.existingOffer?.status === OfferStatus.PUBLISHED;
  }
}
