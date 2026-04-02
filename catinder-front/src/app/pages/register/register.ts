import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { Auth } from '../../services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(Auth);
  private readonly router = inject(Router);

  protected readonly isSubmitting = signal(false);
  protected readonly serverError = signal<string | null>(null);
  protected readonly submitted = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    username: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]],
    acceptTerms: [false, [Validators.requiredTrue]],
  }, { validators: [this.matchPasswordsValidator()] });

  protected readonly usernameErrors = computed(() => this.getControlErrors('username'));
  protected readonly emailErrors = computed(() => this.getControlErrors('email'));
  protected readonly passwordErrors = computed(() => this.getControlErrors('password'));
  protected readonly confirmPasswordErrors = computed(() => this.getConfirmPasswordErrors());

  protected async submit(): Promise<void> {
    this.submitted.set(true);
    this.serverError.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);

    const { username, email, password } = this.form.getRawValue();

    this.auth.register({ username, email, password }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        void this.router.navigateByUrl('/login');
      },
      error: () => {
        this.isSubmitting.set(false);
        this.serverError.set('Impossible de créer le compte. Vérifie les données envoyées ou le backend.');
      },
    });
  }

  private getControlErrors(controlName: 'username' | 'email' | 'password'): string[] {
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
      messages.push(controlName === 'username'
        ? 'Le pseudo doit contenir au moins 2 caractères.'
        : 'Le mot de passe doit contenir au moins 8 caractères.');
    }

    return messages;
  }

  private getConfirmPasswordErrors(): string[] {
    const control = this.form.controls.confirmPassword;
    const errors = control.errors;

    if ((!this.submitted() && !control.touched) || !errors) {
      const formError = this.form.errors?.['passwordMismatch'];
      return formError && (this.submitted() || control.touched) ? ['Les mots de passe ne correspondent pas.'] : [];
    }

    const messages: string[] = [];

    if (errors['required']) {
      messages.push('Ce champ est requis.');
    }

    if (this.form.errors?.['passwordMismatch']) {
      messages.push('Les mots de passe ne correspondent pas.');
    }

    return messages;
  }

  private matchPasswordsValidator(): ValidatorFn {
    return (group: AbstractControl): ValidationErrors | null => {
      const password = group.get('password')?.value as string | null;
      const confirmPassword = group.get('confirmPassword')?.value as string | null;

      if (!password || !confirmPassword) {
        return null;
      }

      return password === confirmPassword ? null : { passwordMismatch: true };
    };
  }
}
