import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-comment-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.scss']
})
export class CommentFormComponent {
  @Output() commentSubmit = new EventEmitter<{ name: string, commentText: string }>(); // Changed 'comment' to 'commentText'
  commentForm: FormGroup;
  isSubmitting = false;

  constructor(private fb: FormBuilder) {
    this.commentForm = this.fb.group({
      name: ['', Validators.required],
      commentText: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.commentForm.valid) {
      this.isSubmitting = true;
      this.commentForm.disable();
      this.commentSubmit.emit(this.commentForm.value);
    }
  }
}
