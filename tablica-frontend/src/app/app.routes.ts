import { Routes } from '@angular/router';
import { AuthComponent } from './components/auth/auth.component';
import {authGuard} from './auth.guard';
import { OfferListComponent } from './components/offer-list/offer-list.component';

export const routes: Routes = [
  { path: 'auth', component: AuthComponent },
  // { path: 'offers', component: OfferListComponent },
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
        path: 'my-offers',
        loadComponent: () => import('./components/user-offer-list/user-offer-list.component').then(m => m.UserOfferListComponent)
      },
    ]
  },
  { path: '**', redirectTo: 'offers' },
];
