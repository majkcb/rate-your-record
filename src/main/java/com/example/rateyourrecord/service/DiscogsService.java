package com.example.rateyourrecord.service;

import com.example.rateyourrecord.client.DiscogsClient;
import com.example.rateyourrecord.model.DiscogsReleaseResponse;
import com.example.rateyourrecord.model.DiscogsResult;
import com.example.rateyourrecord.model.Release;
import com.example.rateyourrecord.model.DiscogsReleaseResponse.DiscogsArtist;
import com.example.rateyourrecord.model.DiscogsReleaseResponse.DiscogsImage;
import com.example.rateyourrecord.model.DiscogsReleaseResponse.DiscogsTrack;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DiscogsService {

    private final DiscogsClient discogsClient;

    public DiscogsService(DiscogsClient discogsClient) {
        this.discogsClient = discogsClient;
    }

    public Mono<List<Release>> searchReleases(String query) {
        return discogsClient.searchReleases(query)
                .map(discogsResults -> discogsResults.stream()
                        .map(this::mapDiscogsResultToRelease)
                        .toList()
                );
    }

    public Mono<Release> getReleaseDetails(String discogsReleaseId) {
        return discogsClient.getReleaseById(discogsReleaseId)
                .map(this::mapDiscogsReleaseResponseToRelease);
    }

    private Release mapDiscogsResultToRelease(DiscogsResult discogsResult) {
        Release release = new Release();
        release.setId(String.valueOf(discogsResult.id()));
        release.setTitle(discogsResult.title());
        release.setArtist(discogsResult.artist());
        release.setCoverImageUrl(discogsResult.coverImage());
        release.setReleaseYear(String.valueOf(discogsResult.year()));
        release.setLastUpdated(LocalDateTime.now());

        Set<String> genres = new HashSet<>();
        Optional.ofNullable(discogsResult.genre()).ifPresent(genres::addAll);
        Optional.ofNullable(discogsResult.style()).ifPresent(genres::addAll);
        release.setGenres(genres);

        release.setAverageRating(0.0);
        release.setNumberOfRatings(0L);
        release.setTracklist(Collections.emptyList());
        release.setTotalTime("N/A");
        release.setDescription("No detailed description available from search results.");

        return release;
    }

    private Release mapDiscogsReleaseResponseToRelease(DiscogsReleaseResponse discogsResponse) {
        Release release = new Release();
        release.setId(String.valueOf(discogsResponse.id()));
        release.setTitle(discogsResponse.title());
        release.setArtist(getArtistFromArtistsList(discogsResponse.artists()));
        release.setCoverImageUrl(getCoverImageFromImagesList(discogsResponse.images()));
        release.setTracklist(getTracklistFromDiscogsTracks(discogsResponse.tracklist()));
        release.setGenres(getGenresFromDiscogsResponse(discogsResponse.genres(), discogsResponse.styles()));
        release.setReleaseYear(String.valueOf(discogsResponse.year()));
        release.setTotalTime(calculateTotalTimeFromTracklist(discogsResponse.tracklist()));
        release.setDescription(discogsResponse.notes() != null ? discogsResponse.notes() : "No description available.");
        release.setLastUpdated(LocalDateTime.now());

        release.setAverageRating(0.0);
        release.setNumberOfRatings(0L);

        return release;
    }

    private String getArtistFromArtistsList(List<DiscogsArtist> artists) {
        return Optional.ofNullable(artists)
                .filter(list -> !list.isEmpty())
                .map(list -> list.getFirst().name())
                .orElse("Unknown Artist");
    }

    private String getCoverImageFromImagesList(List<DiscogsImage> images) {
        return Optional.ofNullable(images)
                .filter(list -> !list.isEmpty())
                .map(list -> list.getFirst().uri())
                .orElse(null);
    }

    private List<String> getTracklistFromDiscogsTracks(List<DiscogsTrack> tracklist) {
        return Optional.ofNullable(tracklist)
                .orElse(List.of())
                .stream()
                .map(t -> "%s. %s (%s)".formatted(t.position(), t.title(), t.duration()))
                .toList();
    }


    private Set<String> getGenresFromDiscogsResponse(List<String> genres, List<String> styles) {
        return Stream.concat(
                Optional.ofNullable(genres).orElse(Collections.emptyList()).stream(),
                Optional.ofNullable(styles).orElse(Collections.emptyList()).stream()
        ).collect(Collectors.toSet());
    }

    private String calculateTotalTimeFromTracklist(List<DiscogsTrack> tracklist) {
        if (tracklist == null) {
            return "N/A";
        }

        long totalSeconds = tracklist.stream()
                .map(DiscogsTrack::duration)
                .mapToLong(this::parseDurationToSeconds)
                .sum();

        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    private long parseDurationToSeconds(String duration) {
        if (duration == null || duration.isEmpty()) {
            return 0;
        }
        try {
            String[] parts = duration.split(":");
            if (parts.length == 2) {
                return Long.parseLong(parts[0]) * 60 + Long.parseLong(parts[1]);
            } else {
                throw new IllegalArgumentException("Invalid duration format: " + duration);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid duration format: " + duration, e);
        }
    }
}
