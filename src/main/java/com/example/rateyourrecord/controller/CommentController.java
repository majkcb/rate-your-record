package com.example.rateyourrecord.controller;

import com.example.rateyourrecord.dto.CommentRequest;
import com.example.rateyourrecord.model.Comment;
import com.example.rateyourrecord.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/releases/{id}/comments")
    public Mono<ResponseEntity<Comment>> addComment(
            @PathVariable String id,
            @Valid @RequestBody CommentRequest commentRequest) {
        return commentService.addCommentToRelease(id, commentRequest.name(), commentRequest.commentText())
                .map(comment -> ResponseEntity.status(HttpStatus.CREATED).body(comment))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/releases/{id}/comments")
    public Mono<ResponseEntity<List<Comment>>> getCommentsForRelease(@PathVariable String id) {
        return commentService.getCommentsForRelease(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
