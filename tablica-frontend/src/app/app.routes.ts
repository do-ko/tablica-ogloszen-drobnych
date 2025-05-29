import { Routes } from '@angular/router';
import { AuthComponent } from './components/auth/auth.component';
import {authGuard} from './auth.guard';
import { OfferListComponent } from './components/offer-list/offer-list.component';

export const routes: Routes = [
  { path: 'auth', component: AuthComponent },
  {
    path: '',
    canActivate: [authGuard],
    children: [
      {
        path: 'offers',
        loadComponent: () => import('./components/offer-list/offer-list.component').then(m => m.OfferListComponent)
      },
      {
        path: 'offers/create',
        loadComponent: () => import('./components/create-edit-offer/create-edit-offer.component').then(m => m.CreateEditOfferComponent)
      },
      {
        path: 'offers/:id/details',
        loadComponent: () => import('./components/offer-detail/offer-detail.component').then(m => m.OfferDetailComponent)
      },
      {
        path: 'offers/edit/:id',
        loadComponent: () => import('./components/create-edit-offer/create-edit-offer.component').then(m => m.CreateEditOfferComponent)
      },
      {
        path: 'my-offers',
        loadComponent: () => import('./components/user-offer-list/user-offer-list.component').then(m => m.UserOfferListComponent)
      },
      {
        path: 'contact-data',
        loadComponent: () => import('./components/contact-data-form/contact-data-form.component').then(m => m.ContactDataFormComponent)
      },
    ]
  },
  { path: '**', redirectTo: 'offers' },
];
