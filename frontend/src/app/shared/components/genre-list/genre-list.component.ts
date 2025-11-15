import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Genre } from '../../../core/models/genre.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-genre-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './genre-list.component.html',
  styleUrls: ['./genre-list.component.scss']
})
export class GenreListComponent {
  @Input() genres: Genre[] = [];
  @Input() selectedGenre: string = '';
  @Output() genreSelected = new EventEmitter<string>();
}
