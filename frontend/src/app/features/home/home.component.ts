import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { GenreListComponent } from '../../shared/components/genre-list/genre-list.component';
import { ReleaseCardComponent } from '../../shared/components/release-card/release-card.component';
import { HomeService } from '../../core/services/home.service';
import { HomeData } from '../../core/models/home-data.model';
import { Observable, combineLatest, of } from 'rxjs';
import { debounceTime, startWith, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, GenreListComponent, ReleaseCardComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  private homeService = inject(HomeService);

  searchControl = new FormControl<string>('');
  genreControl = new FormControl<string | null>(null);

  data$: Observable<HomeData> = of({ releases: [], genres: [], search: '', genre: '' });

  ngOnInit() {
    this.data$ = combineLatest([
      this.searchControl.valueChanges.pipe(startWith(this.searchControl.value || '')),
      this.genreControl.valueChanges.pipe(startWith(this.genreControl.value || null))
    ]).pipe(
      debounceTime(300),
      switchMap(([search, genre]) => this.homeService.getHomeData(search || undefined, genre || undefined))
    );
  }

  onGenreSelected(genre: string) {
    this.genreControl.setValue(genre);
  }
}
