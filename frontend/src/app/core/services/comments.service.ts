import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment } from '../models/comment.model';

@Injectable({
  providedIn: 'root'
})
export class CommentsService {
  private http = inject(HttpClient);

  postComment(releaseId: string, comment: { name: string, commentText: string }): Observable<Comment> {
    return this.http.post<Comment>(`/api/releases/${releaseId}/comments`, comment);
  }

  getCommentsForRelease(releaseId: string): Observable<Comment[]> {
    return this.http.get<Comment[]>(`/api/releases/${releaseId}/comments`);
  }
}
