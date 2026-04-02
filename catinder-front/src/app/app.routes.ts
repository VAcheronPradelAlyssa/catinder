import { Routes } from '@angular/router';

// Routes minimales pour l’auth et l’expérience Catinder.
export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then(m => m.Login),
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register').then(m => m.Register),
  },
  {
    path: 'swipe',
    canActivate: [() => import('./guards/auth.guard').then(m => m.authGuard)],
    loadComponent: () => import('./pages/swipe/swipe').then(m => m.Swipe),
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
