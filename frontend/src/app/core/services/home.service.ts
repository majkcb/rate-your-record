import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { HomeData } from '../models/home-data.model';
import { ReleasesService } from './releases.service';
import { GenreService } from './genre.service';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  private http = inject(HttpClient);
  private releasesService = inject(ReleasesService);
  private genreService = inject(GenreService);

  getHomeData(search?: string, genre?: string): Observable<HomeData> {
    let releasesObservable: Observable<any>;

    if (search && search.trim() !== '') {
      releasesObservable = this.releasesService.searchReleases(search);
    } else if (genre && genre.trim() !== '') {
      releasesObservable = this.releasesService.getReleasesByGenre(genre);
    } else {
      releasesObservable = this.releasesService.getAllReleases();
    }

    const genresObservable = this.genreService.getGenres();

    return forkJoin({
      releases: releasesObservable,
      genres: genresObservable
    }).pipe(
      map(data => ({
        releases: data.releases,
        genres: data.genres,
        search: search || '',
        genre: genre || ''
      }))
    );
  }

}
