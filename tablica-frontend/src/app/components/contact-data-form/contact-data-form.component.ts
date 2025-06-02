import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import {Router, RouterLink} from '@angular/router';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-contact-data-form',
  templateUrl: './contact-data-form.component.html',
  styleUrls: ['./contact-data-form.component.scss'],
  imports: [
    ReactiveFormsModule,
    CommonModule,
    HeaderComponent,
    RouterLink
  ],
  standalone: true
})
export class ContactDataFormComponent implements OnInit {
  contactForm: FormGroup;
  currentUser: User | null = null;
  isLoading = false;
  isSuccess = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {
    this.contactForm = this.fb.group({
      email: ['', [Validators.email]],
      phone: ['', [Validators.pattern('^\\+?[1-9]\\d{1,14}$')]]
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();

    this.contactForm.patchValue({
      email: this.currentUser?.contactData?.email ?? "",
      phone: this.currentUser?.contactData?.phone ?? ""
    });
  }

  onSubmit(): void {
    if (this.contactForm.invalid) {
      return;
    }

    if (!this.currentUser?.userId) {
      this.error = 'User information is missing. Please log in again.';
      return;
    }

    this.isLoading = true;
    this.error = null;
    this.isSuccess = false;

    const formData = this.contactForm.value;

    this.userService.changeContactData(this.currentUser.userId, formData)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (response) => {
          this.isSuccess = true;
          const updatedUser = response.user;
          this.authService.updateCurrentUser(updatedUser);
          this.currentUser = this.authService.getCurrentUser();
        },
        error: (error) => {
          console.error('Error updating contact data', error);
          this.error = error.error?.message || 'Could not update contact information. Please try again later.';
        }
      });
  }
}
