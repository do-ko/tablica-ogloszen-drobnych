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
    ]
  },
  { path: '**', redirectTo: 'offers' },
];
