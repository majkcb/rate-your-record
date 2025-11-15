import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Release } from '../models/release.model';
import { ReleaseSummary } from '../models/release-summary.model';

@Injectable({
  providedIn: 'root'
})
export class ReleasesService {
  private http = inject(HttpClient);

  getAllReleases(): Observable<ReleaseSummary[]> {
    return this.http.get<ReleaseSummary[]>('/api/releases');
  }

  getRelease(id: string): Observable<Release> {
    return this.http.get<Release>(`/api/releases/${id}`);
  }

  rateRelease(id: string, rating: number): Observable<any> {
    return this.http.post(`/api/releases/${id}/rating`, { rating });
  }

  searchReleases(query: string): Observable<ReleaseSummary[]> {
    let params = new HttpParams().set('search', query);
    return this.http.get<ReleaseSummary[]>('/api/home', { params });
  }

  getReleasesByGenre(genre: string): Observable<ReleaseSummary[]> {
    let params = new HttpParams().set('genre', genre);
    return this.http.get<ReleaseSummary[]>('/api/home', { params });
  }
}
