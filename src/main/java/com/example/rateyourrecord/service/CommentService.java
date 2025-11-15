package com.example.rateyourrecord.service;

import com.example.rateyourrecord.model.Comment;
import com.example.rateyourrecord.model.Release;
import com.example.rateyourrecord.repository.CommentRepository;
import com.example.rateyourrecord.repository.ReleaseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReleaseRepository releaseRepository;

    public CommentService(CommentRepository commentRepository, ReleaseRepository releaseRepository) {
        this.commentRepository = commentRepository;
        this.releaseRepository = releaseRepository;
    }

    public Mono<Comment> addCommentToRelease(String releaseId, String name, String commentText) {
        return Mono.fromCallable(() -> releaseRepository.findById(releaseId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalRelease -> {
                    if (optionalRelease.isPresent()) {
                        Release release = optionalRelease.get();
                        Comment comment = new Comment();
                        comment.setName(name);
                        comment.setCommentText(commentText);
                        comment.setRelease(release);
                        return Mono.fromCallable(() -> commentRepository.save(comment))
                                .subscribeOn(Schedulers.boundedElastic());
                    }
                    return Mono.empty(); // Release not found
                });
    }

    public Mono<List<Comment>> getCommentsForRelease(String releaseId) {
        return Mono.fromCallable(() -> commentRepository.findByReleaseIdOrderByCreatedAtDesc(releaseId))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
