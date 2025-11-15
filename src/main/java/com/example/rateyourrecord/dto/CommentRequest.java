package com.example.rateyourrecord.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(
        @NotBlank(message = "Name cannot be empty")
        String name,
        @NotBlank(message = "Comment cannot be empty")
        String commentText
) {}
