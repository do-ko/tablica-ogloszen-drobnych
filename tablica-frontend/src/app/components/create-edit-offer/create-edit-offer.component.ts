import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { OfferService } from '../../services/offer.service';
import { AuthService } from '../../services/auth.service';
import {Offer, OfferImage, OfferStatus} from '../../models/offer.model';
import { HeaderComponent } from '../header/header.component';
import { User } from '../../models/user.model';
import { finalize } from 'rxjs/operators';
import {environment} from '../../enviroment';


interface ImageFile {
  file: File;
  preview: string;
}

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
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  offerForm: FormGroup;
  isEditMode = false;
  existingOffer: Offer | null = null;
  isLoading = false;
  errorMessage = '';
  selectedTags: string[] = [];
  tagSuggestions: string[] = [];
  currentUser: User | null = null;

  selectedImages: ImageFile[] = [];
  isDragActive = false;
  imageUploadError = '';
  maxFileSize = 5 * 1024 * 1024;
  maxFiles = 10;
  allowedTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];

  existingImages: OfferImage[] = [];
  imagesToDelete: string[] = [];

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
      contactEmail: [this.currentUser?.contactData?.email ?? ""],
      contactPhone: [this.currentUser?.contactData?.phone ?? ""],
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

          if (this.isEditMode && offer.images && offer.images.length > 0) {
            this.existingImages = offer.images;
          }

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

    this.updateEmailControlState(this.offerForm.get('contactEmail')?.value);
    this.updatePhoneControlState(this.offerForm.get('contactPhone')?.value);
  }

  private updateEmailControlState(email: string): void {
    const showEmailControl = this.offerForm.get('showEmail');

    if (showEmailControl === null) {
      return;
    }

    if (!email || email.trim() === '') {
      showEmailControl.disable();
      showEmailControl.setValue(false);
    } else {
      showEmailControl.enable();
    }
  }

  private updatePhoneControlState(phone: string): void {
    const showPhoneControl = this.offerForm.get('showPhone');

    if (showPhoneControl === null) {
      return;
    }

    if (!phone || phone.trim() === '') {
      showPhoneControl.disable();
      showPhoneControl.setValue(false);
    } else {
      showPhoneControl.enable();
    }
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

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragActive = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragActive = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragActive = false;

    if (event.dataTransfer?.files) {
      this.handleFiles(event.dataTransfer.files);
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.handleFiles(input.files);
    }
  }

  handleFiles(files: FileList): void {
    this.imageUploadError = '';

    // Sprawdź czy nie przekroczono limitu plików
    if (this.selectedImages.length + files.length > this.maxFiles) {
      this.imageUploadError = `Możesz dodać maksymalnie ${this.maxFiles} obrazów.`;
      return;
    }

    Array.from(files).forEach(file => {
      // Sprawdź typ pliku
      if (!this.allowedTypes.includes(file.type)) {
        this.imageUploadError = 'Dozwolone są tylko obrazy w formatach: JPG, PNG, WebP i GIF.';
        return;
      }

      // Sprawdź rozmiar pliku
      if (file.size > this.maxFileSize) {
        this.imageUploadError = 'Maksymalny rozmiar pliku to 5MB.';
        return;
      }

      // Utwórz podgląd obrazu
      const reader = new FileReader();
      reader.onload = (e: ProgressEvent<FileReader>) => {
        this.selectedImages.push({
          file: file,
          preview: e.target?.result as string
        });
      };
      reader.readAsDataURL(file);
    });

    // Zresetuj input po wybraniu plików
    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
  }

  removeImage(index: number): void {
    this.selectedImages.splice(index, 1);
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
      email: formValue.showEmail ? formValue.contactEmail : '',
      phone: formValue.showPhone ? formValue.contactPhone : '',
      sellerId: this.currentUser?.userId || '',
      status: status
    };

    if (this.isEditMode && this.existingOffer) {
      this.offerService.updateOffer(this.existingOffer.offerId, offerData).subscribe({
        next: (updatedOffer) => {
          this.processImageChanges(this.existingOffer!.offerId);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = 'Wystąpił błąd podczas aktualizacji oferty.';
          console.error('Błąd aktualizacji oferty', error);
        }
      });
    } else {
      this.offerService.createOffer(offerData as any).subscribe({
        next: (createdOffer) => {
          if (this.selectedImages.length > 0) {
            this.uploadImages(createdOffer.offerId);
          } else {
            this.isLoading = false;
            this.router.navigate(['/my-offers']);
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = 'Wystąpił błąd podczas tworzenia oferty.';
          console.error('Błąd tworzenia oferty', error);
        }
      });
    }
  }

  private uploadImages(offerId: string): void {
    const formData = new FormData();
    this.selectedImages.forEach(imageFile => {
      formData.append('images', imageFile.file);
    });

    this.offerService.addImagesToOffer(offerId, formData)
      .pipe(finalize(() => {
        this.isLoading = false;
        this.router.navigate(['/my-offers']);
      }))
      .subscribe({
        error: (error) => {
          console.error('Error uploading images', error);
          this.errorMessage = 'Offer was created but there was an error uploading images.';
        }
      });
  }

  private processImageChanges(offerId: string): void {
    if (this.imagesToDelete.length > 0) {
      this.offerService.deleteOfferImages(offerId, this.imagesToDelete).subscribe({
        next: () => {
          if (this.selectedImages.length > 0) {
            this.uploadImages(offerId);
          } else {
            this.isLoading = false;
            this.router.navigate(['/my-offers']);
          }
        },
        error: (error) => {
          console.error('Błąd podczas usuwania obrazów', error);
          this.errorMessage = 'Wystąpił błąd podczas usuwania obrazów.';
          this.isLoading = false;
        }
      });
    } else if (this.selectedImages.length > 0) {
      this.uploadImages(offerId);
    } else {
      this.isLoading = false;
      this.router.navigate(['/my-offers']);
    }
  }

  removeExistingImage(imageId: string): void {
    this.existingImages = this.existingImages.filter(img => img.id !== imageId);
    this.imagesToDelete.push(imageId);
  }

  getImageUrl(path: string): string {
    return `${environment.imagesUrl}/${path}`;
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
