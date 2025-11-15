package com.example.rateyourrecord.controller;

import com.example.rateyourrecord.model.Release;
import com.example.rateyourrecord.service.ReleaseService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReleaseController {

    private final ReleaseService releaseService;

    public ReleaseController(ReleaseService releaseService) {
        this.releaseService = releaseService;
    }

    @GetMapping("/home")
    public Mono<ResponseEntity<List<Release>>> getHomeReleases(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre) {

        if (search != null && !search.isEmpty()) {
            return releaseService.searchReleases(search)
                    .map(ResponseEntity::ok);
        } else if (genre != null && !genre.isEmpty()) {
            return releaseService.getReleasesByGenre(genre)
                    .map(ResponseEntity::ok);
        } else {
            return releaseService.getAllReleases()
                    .map(ResponseEntity::ok);
        }
    }

    @GetMapping("/releases")
    public Mono<ResponseEntity<List<Release>>> getAllReleases() {
        return releaseService.getAllReleases()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/releases/{id}")
    public Mono<ResponseEntity<Release>> getReleaseById(@PathVariable String id) {
        return releaseService.getReleaseById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/releases/{id}/rating")
    public Mono<ResponseEntity<String>> rateRelease(@PathVariable String id, @RequestBody RatingRequest ratingRequest) {
        return releaseService.updateReleaseRating(id, ratingRequest.getRating())
                .map(release -> ResponseEntity.ok("{\"status\": \"ok\"}"))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Setter
    @Getter
    static class RatingRequest {
        private int rating;
    }
}
