import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StarRatingComponent } from '../star-rating/star-rating.component';
import { RouterModule } from '@angular/router';
import { ReleaseSummary } from '../../../core/models/release-summary.model';

@Component({
  selector: 'app-release-card',
  standalone: true,
  imports: [CommonModule, StarRatingComponent, RouterModule],
  templateUrl: './release-card.component.html',
  styleUrls: ['./release-card.component.scss']
})
export class ReleaseCardComponent {
  @Input() release!: ReleaseSummary;
}
