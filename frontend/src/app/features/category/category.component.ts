import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { GenreListComponent } from '../../shared/components/genre-list/genre-list.component';
import { ReleaseCardComponent } from '../../shared/components/release-card/release-card.component';
import { HomeService } from '../../core/services/home.service';
import { HomeData } from '../../core/models/home-data.model';
import { Observable, combineLatest, of } from 'rxjs';
import { debounceTime, startWith, switchMap, tap } from 'rxjs/operators';

@Component({
  selector: 'app-category',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, GenreListComponent, ReleaseCardComponent],
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.scss']
})
export class CategoryComponent implements OnInit {
  private homeService = inject(HomeService);
  private route = inject(ActivatedRoute);

  searchControl = new FormControl<string>('');
  genreControl = new FormControl<string | null>(null);

  data$: Observable<HomeData> = of({ releases: [], genres: [], search: '', genre: '' });

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.genreControl.setValue(params.get('category'));
    });

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
