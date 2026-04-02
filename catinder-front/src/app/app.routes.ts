import { Routes } from '@angular/router';

// TODO: Ajouter un AuthGuard pour la route swipe si besoin
export const routes: Routes = [
	{
		path: '',
		redirectTo: 'login',
		pathMatch: 'full',
	},
	{
		path: 'login',
		loadComponent: () => import('./pages/login/login').then(m => m.LoginPage),
	},
	{
		path: 'register',
		loadComponent: () => import('./pages/register/register').then(m => m.RegisterPage),
	},
	{
		path: 'swipe',
		loadComponent: () => import('./pages/swipe/swipe').then(m => m.SwipePage),
		// canActivate: [AuthGuard], // Décommente si AuthGuard implémenté
	},
	{
		path: '**',
		redirectTo: 'login',
	},
];
import { Routes } from '@angular/router';

export const routes: Routes = [];
