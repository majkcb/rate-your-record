package com.example.rateyourrecord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DiscogsSearchResultDto(
        int id,
        String title,
        Integer year,
        List<String> genre,
        List<String> style,
        String country,
        @JsonProperty("cover_image") String coverImage
) {}
