import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Auth } from './auth';

@Injectable({
  providedIn: 'root',
})
export class CatService {
  constructor(
    private http: HttpClient,
    private auth: Auth,
  ) {}

  // Récupère le prochain chat à swiper depuis le backend.
  getNextCat(): Observable<Cat> {
    return this.http
      .get<BackendCatDto>(`${environment.API_URL}/cats/next`, { headers: this.auth.authHeaders() })
      .pipe(
        map(cat => ({
          id: cat.id,
          name: cat.name,
          // Le backend ne fournit pas encore age/race, on conserve une UI stable.
          age: 0,
          breed: undefined,
          description: cat.bio ?? '',
          imageUrl: cat.photoUrl,
        })),
      );
  }

  // Envoie le swipe (like/dislike) et retourne l'eventuel match.
  swipe(catId: string | number, liked: boolean): Observable<SwipeResponse> {
    return this.http.post<SwipeResponse>(
      `${environment.API_URL}/swipes`,
      { catId, liked },
      { headers: this.auth.authHeaders() },
    );
  }
}

interface BackendCatDto {
  id: string | number;
  name: string;
  bio?: string;
  photoUrl?: string;
}

export interface SwipeResponse {
  catId: string | number;
  liked: boolean;
  match?: boolean;
}

// Type partagé avec Swipe
export interface Cat {
  id: string | number;
  name: string;
  age: number;
  breed?: string;
  description?: string;
  imageUrl?: string;
}
