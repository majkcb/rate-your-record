package com.example.rateyourrecord.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DiscogsReleaseResponse(
        int id,
        String title,
        Integer year,
        List<String> genres,
        List<String> styles,
        String country,
        String notes,
        List<DiscogsArtist> artists,
        List<DiscogsImage> images,
        List<DiscogsTrack> tracklist
) {
    public record DiscogsArtist(
            int id,
            String name,
            @JsonProperty("anv") String artistNameVariation,
            @JsonProperty("resource_url") String resourceUrl
    ) {}
    public record DiscogsImage(
            String type,
            String uri,
            @JsonProperty("resource_url") String resourceUrl,
            String uri150,
            int width,
            int height
    ) {}
    public record DiscogsTrack(
            String position,
            @JsonProperty("type_") String type,
            String title,
            String duration
    ) {}
}
