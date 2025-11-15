package com.example.rateyourrecord.repository;

import com.example.rateyourrecord.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByReleaseIdOrderByCreatedAtDesc(String releaseId);
}
