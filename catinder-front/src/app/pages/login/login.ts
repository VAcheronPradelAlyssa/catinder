import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(Auth);
  private readonly router = inject(Router);

  protected readonly isSubmitting = signal(false);
  protected readonly serverError = signal<string | null>(null);
  protected readonly submitted = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    rememberMe: [true],
  });

  protected readonly emailErrors = computed(() => this.getControlErrors('email'));
  protected readonly passwordErrors = computed(() => this.getControlErrors('password'));

  protected async submit(): Promise<void> {
    this.submitted.set(true);
    this.serverError.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);

    const { email, password, rememberMe } = this.form.getRawValue();

    this.auth.login({ email, password, rememberMe }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        void this.router.navigateByUrl('/swipe');
      },
      error: () => {
        this.isSubmitting.set(false);
        this.serverError.set('Connexion impossible. Vérifie ton email, ton mot de passe ou le backend.');
      },
    });
  }

  private getControlErrors(controlName: 'email' | 'password'): string[] {
    const control = this.form.controls[controlName];

    if (!this.submitted() && !control.touched) {
      return [];
    }

    const errors = control.errors;

    if (!errors) {
      return [];
    }

    const messages: string[] = [];

    if (errors['required']) {
      messages.push('Ce champ est requis.');
    }

    if (errors['email']) {
      messages.push('L’adresse email doit être valide.');
    }

    if (errors['minlength']) {
      messages.push('Le mot de passe doit contenir au moins 8 caractères.');
    }

    return messages;
  }
}
