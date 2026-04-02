import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CatService {
  constructor(private http: HttpClient) {}

  // Liste cyclique des photos de profils
  private readonly catPhotos = [
    'assets/IMG-20221031-WA0011.jpg',
    'assets/IMG-20221222-WA0015.jpg',
    'assets/popotin.jpg',
    'assets/PXL_20230301_220705296.jpg',
  ];
  private photoIndex = 0;

  // Récupère le prochain chat à swiper (dummy cyclique)
  getNextCat(): Observable<Cat> {
    const photos = this.catPhotos;
    const idx = this.photoIndex++ % photos.length;
    const names = ['Miaou', 'Gribouille', 'Popotin', 'Pixel'];
    const breeds = ['Européen', 'Chartreux', 'Siamois', 'Bengal'];
    const descs = [
      'Un chat joueur et câlin.',
      'Adore les caresses et les boîtes.',
      'Toujours prêt pour une sieste.',
      'Explorateur et gourmet.',
    ];
    return of({
      id: idx,
      name: names[idx],
      age: 1 + idx,
      breed: breeds[idx],
      description: descs[idx],
      imageUrl: photos[idx],
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
