package com.example.rateyourrecord.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DiscogsResult(
        int id,
        String title,
        Integer year,
        List<String> genre,
        List<String> style,
        String country,
        @JsonProperty("cover_image") String coverImage,
        @JsonProperty("artist") String artist
) {}
