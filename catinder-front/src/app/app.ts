import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

// Composant racine standalone Angular 21 pour Catinder.
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class AppComponent {
  // Le shell racine reste volontairement léger.
}
