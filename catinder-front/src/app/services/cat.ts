import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CatService {
  constructor(private http: HttpClient) {}

  // Récupère le prochain chat à swiper
  getNextCat(): Observable<Cat> {
    // À brancher sur le backend réel :
    // return this.http.get<Cat>(`${environment.API_URL}/cats/next`);
    // Dummy pour dev :
    return of({
      id: Math.random().toString(36).slice(2),
      name: 'Miaou',
      age: 2,
      breed: 'Européen',
      description: 'Un chat joueur et câlin.',
      imageUrl: 'https://cataas.com/cat?width=300&height=300&v=' + Date.now(),
    });
  }

  // Envoie le swipe (like/dislike)
  swipe(catId: string | number, liked: boolean): Observable<any> {
    // return this.http.post(`${environment.API_URL}/swipes`, { catId, liked });
    // Dummy pour dev :
    return of({ success: true });
  }
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
