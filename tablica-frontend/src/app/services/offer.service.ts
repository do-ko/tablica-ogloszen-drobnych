import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {Offer, OfferStatus} from '../models/offer.model';

@Injectable({
  providedIn: 'root'
})
export class OfferService {
  private offersSubject = new BehaviorSubject<Offer[]>([]);
  public offers$ = this.offersSubject.asObservable();
  private tagsSubject = new BehaviorSubject<string[]>(['elektronika', 'dom', 'sport', 'książki', 'muzyka']);

  constructor() {
    this.initializeSampleData();
  }

  private initializeSampleData(): void {
    const sampleOffers: Offer[] = [
      {
        offerId: '1',
        title: 'Laptop Dell XPS 13',
        description: 'Bardzo dobry stan, używany 2 lata, sprzedaję z powodu zakupu nowego modelu. W zestawie ładowarka. Nie ma rys na ekranie. Wszystko działa jak należy. W razie pytań proszę o kontakt.',
        images: [],
        tags: ['elektronika', 'komputer'],
        contactInfo: {
          email: 'seller@example.com',
          phone: '+48123456789',
          showEmail: true,
          showPhone: false
        },
        sellerId: '1',
        status: OfferStatus.PUBLISHED,
        createdAt: new Date(),
        updatedAt: new Date()
      },
    ];
    this.offersSubject.next(sampleOffers);
  }

  getOffers(): Observable<Offer[]> {
    return this.offers$;
  }

  getPublishedOffers(): Observable<Offer[]> {
    return new Observable(observer => {
      this.offers$.subscribe(offers => {
        observer.next(offers.filter(l => l.status === OfferStatus.PUBLISHED));
      });
    });
  }

  getUserOffers(userId: string): Observable<Offer[]> {
    return new Observable(observer => {
      this.offers$.subscribe(offers => {
        observer.next(offers.filter(l => l.sellerId === userId));
      });
    });
  }

  createOffer(offer: Omit<Offer, 'id' | 'createdAt' | 'updatedAt'>): Observable<Offer> {
    return new Observable(observer => {
      const newOffer: Offer = {
        ...offer,
        offerId: Date.now().toString(),
        createdAt: new Date(),
        updatedAt: new Date(),
        previewToken: this.generatePreviewToken()
      };

      const currentOffers = this.offersSubject.value;
      this.offersSubject.next([...currentOffers, newOffer]);
      observer.next(newOffer);
      observer.complete();
    });
  }

  updateOffer(id: string, updates: Partial<Offer>): Observable<Offer> {
    return new Observable(observer => {
      const currentOffers = this.offersSubject.value;
      const index = currentOffers.findIndex(l => l.offerId === id);

      if (index !== -1) {
        const updatedOffer = {
          ...currentOffers[index],
          ...updates,
          updatedAt: new Date()
        };

        currentOffers[index] = updatedOffer;
        this.offersSubject.next([...currentOffers]);
        observer.next(updatedOffer);
      }
      observer.complete();
    });
  }

  deleteOffer(id: string): Observable<void> {
    return new Observable(observer => {
      const currentOffers = this.offersSubject.value;
      const filtered = currentOffers.filter(l => l.offerId !== id);
      this.offersSubject.next(filtered);
      observer.complete();
    });
  }

  searchOffers(query: string, tags: string[]): Observable<Offer[]> {
    return new Observable(observer => {
      this.getPublishedOffers().subscribe(offers => {
        let filtered = offers;

        if (query.trim()) {
          const searchTerm = query.toLowerCase();
          filtered = filtered.filter(l =>
            l.title.toLowerCase().includes(searchTerm) ||
            l.description.toLowerCase().includes(searchTerm)
          );
        }

        if (tags.length > 0) {
          filtered = filtered.filter(l =>
            tags.some(tag => l.tags.includes(tag))
          );
        }

        observer.next(filtered);
      });
    });
  }

  getTagSuggestions(input: string): Observable<string[]> {
    return new Observable(observer => {
      const allTags = this.tagsSubject.value;
      const currentOffers = this.offersSubject.value;

      const usedTags = currentOffers.flatMap(l => l.tags);
      const uniqueTags = [...new Set([...allTags, ...usedTags])];

      const filtered = uniqueTags
        .filter(tag => tag.toLowerCase().includes(input.toLowerCase()))
        .sort((a, b) => {
          const countA = usedTags.filter(t => t === a).length;
          const countB = usedTags.filter(t => t === b).length;
          return countB - countA;
        });

      observer.next(filtered);
      observer.complete();
    });
  }

  getOfferByPreviewToken(token: string): Observable<Offer | null> {
    return new Observable(observer => {
      this.offers$.subscribe(offers => {
        const offer = offers.find(l => l.previewToken === token);
        observer.next(offer || null);
      });
    });
  }

  private generatePreviewToken(): string {
    return Math.random().toString(36).substring(2, 15) +
      Math.random().toString(36).substring(2, 15);
  }
}
