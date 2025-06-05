import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Offer, OfferStatus} from '../models/offer.model';
import {environment} from '../enviroment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class OfferService {
  private apiUrl = `${environment.apiUrl}/offer`;

  constructor(private http: HttpClient) {}

  getPublishedOffers(page = 0, size = 10): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  searchOffers(keyword: string, tags: string[], page = 0, size = 10): Observable<any> {
    let params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());

    tags.forEach(tag => {
      params = params.append('tags', tag);
    });

    return this.http.get<any>(this.apiUrl, { params });
  }

  getUserOffers(userId: string): Observable<Offer[]> {
    return this.http.get<any>(`${this.apiUrl}/user/${userId}`).pipe(
      map(response => response.content)
    );
  }

  searchUserOffers(userId: string, query: string, tags: string[]): Observable<Offer[]> {
    let params = new HttpParams().set('keyword', query || '');

    return this.http.get<any>(`${this.apiUrl}/user/${userId}`, { params }).pipe(
      map(response => {
        let offers = response.content;
        if (tags.length > 0) {
          offers = offers.filter((offer: Offer) =>
            tags.some(tag => offer.tags.includes(tag))
          );
        }
        return offers;
      })
    );
  }

  createOffer(offer: Omit<Offer, 'offerId' | 'createdAt' | 'updatedAt'>): Observable<Offer> {
    return this.http.post<any>(`${this.apiUrl}`, offer).pipe(
      map(response => response.offer)
    );
  }

  updateOffer(id: string, updates: Partial<Offer>): Observable<Offer> {
    return this.http.put<Offer>(`${this.apiUrl}/${id}`, updates);
  }

  getOfferById(id: string): Observable<Offer> {
    return this.http.get<Offer>(`${this.apiUrl}/${id}`);
  }

  changeOfferStatus(id: string, status: OfferStatus): Observable<Offer> {
    const params = new HttpParams().set('status', status.toString());
    return this.http.put<Offer>(`${this.apiUrl}/${id}/status`, null, { params });
  }

  getTagSuggestions(input: string): Observable<string[]> {
    return this.http.get<any>(`${this.apiUrl}/tag?page=0&size=10`).pipe(
      map(response => {
        return response.content
          .filter((tagData: any) => tagData.tag.toLowerCase().includes(input.toLowerCase()))
          .map((tagData: any) => tagData.tag);
      })
    );
  }

  addImagesToOffer(offerId: string, formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUrl}/${offerId}/images`, formData);
  }

  deleteOfferImages(offerId: string, imageIds: string[]): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${offerId}/images`, {
      body: imageIds
    });
  }
}
