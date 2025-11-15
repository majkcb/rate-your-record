package com.example.rateyourrecord.client;

import com.example.rateyourrecord.model.DiscogsReleaseResponse;
import com.example.rateyourrecord.model.DiscogsResult;
import com.example.rateyourrecord.model.DiscogsSearchResponse;
import com.example.rateyourrecord.dto.DiscogsSearchResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class DiscogsClient {

    private final WebClient webClient;

    public DiscogsClient(@Qualifier("discogsWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<DiscogsResult>> searchReleases(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/database/search")
                        .queryParam("q", UriUtils.encode(query, StandardCharsets.UTF_8))
                        .queryParam("type", "release")
                        .build())
                .retrieve()
                .bodyToMono(DiscogsSearchResponse.class)
                .map(response -> response == null || response.results() == null
                        ? List.<DiscogsResult>of()
                        : response.results().stream()
                        .map(this::mapToDiscogsResult)
                        .toList())
                .onErrorResume(e -> logAndReturn(
                        "Error searching releases with query", query, e,
                        List.of()
                ));
    }


    public Mono<DiscogsReleaseResponse> getReleaseById(String id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/releases/{id}").build(id))
                .retrieve()
                .bodyToMono(DiscogsReleaseResponse.class)
                .onErrorResume(e -> {
                    DiscogsReleaseResponse fallback = buildFallbackRelease(id, e);
                    return logAndReturn("Error fetching release with ID", id, e, fallback);
                });
    }

    private <T> Mono<T> logAndReturn(String message, Object key, Throwable error, T fallback) {
        log.error("{} {}: {}", message, key, error.getMessage());
        return Mono.just(fallback);
    }

    private DiscogsReleaseResponse buildFallbackRelease(String id, Throwable e) {
        int parsedId;

        try {
            parsedId = Integer.parseInt(id);
        } catch (NumberFormatException nfe) {
            log.warn("Could not parse release ID '{}' to integer for fallback.", id);
            parsedId = -1;
        }

        return new DiscogsReleaseResponse(
                parsedId,
                "Unknown Release " + id,
                null,
                List.of("Unknown"),
                List.of(),
                null,
                "Error fetching details for this release.",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    private DiscogsResult mapToDiscogsResult(DiscogsSearchResultDto dto) {
        String title = dto.title();
        String artist = null;

        if (title != null) {
            String[] parts = title.split(" - ", 2);
            if (parts.length == 2) {
                artist = parts[0];
                title = parts[1];
            }
        }

        return new DiscogsResult(
                dto.id(),
                title,
                dto.year(),
                dto.genre(),
                dto.style(),
                dto.country(),
                dto.coverImage(),
                artist
        );
    }
}