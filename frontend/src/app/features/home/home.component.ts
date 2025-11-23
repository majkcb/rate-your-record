import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { GenreListComponent } from '../../shared/components/genre-list/genre-list.component';
import { ReleaseCardComponent } from '../../shared/components/release-card/release-card.component';
import { HomeService } from '../../core/services/home.service';
import { HomeData } from '../../core/models/home-data.model';
import { Observable, Subject, combineLatest, of } from 'rxjs';
import { catchError, debounceTime, startWith, switchMap, tap } from 'rxjs/operators';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, GenreListComponent, ReleaseCardComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  private homeService = inject(HomeService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  // Set the initial value directly from the snapshot when the component is created.
  searchControl = new FormControl<string>(this.route.snapshot.queryParamMap.get('q') || '', { nonNullable: true });
  genreControl = new FormControl<string | null>(null);

  data$: Observable<HomeData | null> = of(null);
  error: boolean = false;

  private retrySubject = new Subject<void>();

  ngOnInit() {
    const search$ = this.searchControl.valueChanges.pipe(
      startWith(this.searchControl.value),
      debounceTime(300),
      tap(search => this.updateQueryParam(search))
    );

    const genre$ = this.genreControl.valueChanges.pipe(startWith(this.genreControl.value));
    const retry$ = this.retrySubject.asObservable().pipe(startWith(null));

    this.data$ = combineLatest([search$, genre$, retry$]).pipe(
      // Use switchMap to cancel previous pending requests
      switchMap(([search, genre]) =>
        this.homeService.getHomeData(search || undefined, genre || undefined).pipe(
          catchError(() => {
            this.error = true;
            return of(null); // Return a non-error observable to keep the stream alive
          })
        )
      )
    );
  }

  onGenreSelected(genre: string) {
    this.genreControl.setValue(genre);
  }

  retry() {
    this.error = false;
    this.retrySubject.next();
  }

  private updateQueryParam(search: string | null) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { q: search || null },
      queryParamsHandling: 'merge',
      replaceUrl: true
    });
  }
}
