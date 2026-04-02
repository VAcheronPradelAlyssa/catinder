import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { Cat, CatService } from '../../services/cat';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-swipe',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './swipe.html',
  styleUrl: './swipe.scss',
})
export class Swipe {
  private readonly catService = inject(CatService);
  private readonly auth = inject(Auth);

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly cat = signal<Cat | null>(null);
  protected readonly match = signal(false);
  protected readonly feedback = signal<string | null>(null);

  constructor() {
    this.loadNextCat();
  }

  protected loadNextCat() {
    this.loading.set(true);
    this.error.set(null);
    this.match.set(false);
    this.feedback.set(null);

    this.catService.getNextCat().subscribe({
      next: (cat) => {
        this.cat.set(cat);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger un chat. Réessaie plus tard.');
        this.loading.set(false);
      },
    });
  }

  protected like() {
    if (!this.cat()) return;
    this.feedback.set(null);
    this.catService.swipe(this.cat()!.id, true).subscribe({
      next: (response) => {
        if (response.match) {
          this.match.set(true);
          this.feedback.set('MATCH ! 😻');
        } else {
          this.feedback.set('Like envoyé !');
        }
        setTimeout(() => {
          this.loadNextCat();
        }, 1200);
      },
      error: () => {
        this.feedback.set('Erreur lors du like.');
      },
    });
  }

  protected dislike() {
    if (!this.cat()) return;
    this.feedback.set(null);
    this.catService.swipe(this.cat()!.id, false).subscribe({
      next: () => {
        this.feedback.set('Chat ignoré.');
        setTimeout(() => {
          this.loadNextCat();
        }, 700);
      },
      error: () => {
        this.feedback.set('Erreur lors du swipe.');
      },
    });
  }

  protected logout() {
    this.auth.logout();
    window.location.href = '/login';
  }
}