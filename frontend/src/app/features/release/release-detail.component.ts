import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { StarRatingComponent } from '../../shared/components/star-rating/star-rating.component';
import { CommentFormComponent } from '../../shared/components/comment-form/comment-form.component';
import { ReleasesService } from '../../core/services/releases.service';
import { Release } from '../../core/models/release.model';
import { Observable, of, BehaviorSubject, combineLatest } from 'rxjs';
import { switchMap, tap, map } from 'rxjs/operators';
import { CommentsService } from '../../core/services/comments.service';
import { Comment } from '../../core/models/comment.model';

@Component({
  selector: 'app-release-detail',
  standalone: true,
  imports: [CommonModule, StarRatingComponent, CommentFormComponent],
  templateUrl: './release-detail.component.html',
  styleUrls: ['./release-detail.component.scss']
})
export class ReleaseDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private releasesService = inject(ReleasesService);
  private commentsService = inject(CommentsService);
  private location = inject(Location);

  release$: Observable<Release | undefined> = of(undefined);
  comments$: Observable<Comment[]> = of([]);
  private refreshComments$ = new BehaviorSubject<void>(undefined);
  private refreshRelease$ = new BehaviorSubject<void>(undefined);

  ratingSubmitted = false;
  commentSubmitted = false;

  ngOnInit() {
    const releaseIdParam$ = this.route.paramMap.pipe(
      map(params => {
        const idParam = params.get('releaseId');
        if (idParam === null) {
          console.error('Release ID is missing from route parameters.');
          return undefined;
        }
        return idParam;
      })
    );

    this.release$ = combineLatest([
      releaseIdParam$,
      this.refreshRelease$
    ]).pipe(
      switchMap(([idParam, _]) => {
        if (idParam === undefined) {
          return of(undefined);
        }
        return this.releasesService.getRelease(idParam);
      })
    );

    this.comments$ = combineLatest([
      releaseIdParam$,
      this.refreshComments$
    ]).pipe(
      switchMap(([idParam, _]) => {
        if (idParam === undefined) {
          return of([]);
        }
        return this.commentsService.getCommentsForRelease(idParam);
      })
    );
  }

  onRating(releaseId: string, rating: number) {
    if (this.ratingSubmitted) {
      return;
    }

    this.releasesService.rateRelease(releaseId, rating).subscribe({
      next: () => {
        this.ratingSubmitted = true;
        this.refreshRelease$.next();
      },
      error: (err) => {
        console.error('Error submitting rating:', err);
      }
    });
  }

  onComment(commentData: { name: string, commentText: string }) {
    this.release$.subscribe(release => {
      if (release) {
        this.commentsService.postComment(release.id, commentData).subscribe(() => {
          this.commentSubmitted = true;
          this.refreshComments$.next();
        });
      }
    });
  }

  goBack(): void {
    this.location.back();
  }
}
