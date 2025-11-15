package com.example.rateyourrecord.service;

import com.example.rateyourrecord.model.Release;
import com.example.rateyourrecord.repository.ReleaseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;

@Service
public class ReleaseService {

    private final ReleaseRepository releaseRepository;
    private final DiscogsService discogsService;

    public ReleaseService(ReleaseRepository releaseRepository, DiscogsService discogsService) {
        this.releaseRepository = releaseRepository;
        this.discogsService = discogsService;
    }

    public Mono<List<Release>> getAllReleases() {
        return blockingCall(releaseRepository::findAll)
                .flatMap(localReleases -> {
                    if (!localReleases.isEmpty()) {
                        return Mono.just(localReleases);
                    }

                    return discogsService.searchReleases("classic rock")
                            .flatMapMany(this::fetchMissingReleasesFromDiscogs)
                            .collectList();
                });
    }

    public Mono<Release> getReleaseById(String id) {
        return getLocalRelease(id)
                .switchIfEmpty(fetchAndStoreFromDiscogs(id));
    }

    public Mono<List<Release>> searchReleases(String query) {
        return blockingCall(() -> releaseRepository.findByTitleContainingIgnoreCase(query))
                .flatMap(localReleases -> {
                    if (!localReleases.isEmpty()) {
                        return Mono.just(localReleases);
                    }

                    return discogsService.searchReleases(query)
                            .flatMapMany(this::fetchMissingReleasesFromDiscogs)
                            .collectList();
                });
    }

    public Mono<Release> updateReleaseRating(String releaseId, int rating) {
        return getLocalRelease(releaseId)
                .flatMap(release -> {
                    long newNumberOfRatings = release.getNumberOfRatings() + 1;
                    double newAverageRating =
                            ((release.getAverageRating() * release.getNumberOfRatings()) + rating) / newNumberOfRatings;

                    release.setAverageRating(newAverageRating);
                    release.setNumberOfRatings(newNumberOfRatings);
                    release.setLastUpdated(LocalDateTime.now());

                    return saveRelease(release);
                });
    }

    public Mono<List<Release>> getReleasesByGenre(String genre) {
        return blockingCall(() -> releaseRepository.findByGenresContainingIgnoreCase(genre));
    }

    private <T> Mono<T> blockingCall(Callable<T> call) {
        return Mono.fromCallable(call).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Release> saveRelease(Release release) {
        return blockingCall(() -> releaseRepository.save(release));
    }

    private Mono<Boolean> exists(String id) {
        return blockingCall(() -> releaseRepository.existsById(id));
    }

    private Mono<Release> getLocalRelease(String id) {
        return blockingCall(() -> releaseRepository.findById(id))
                .flatMap(opt -> opt.map(Mono::just).orElse(Mono.empty()));
    }

    private Mono<Release> fetchAndStoreFromDiscogs(String id) {
        return discogsService.getReleaseDetails(id)
                .flatMap(this::saveRelease);
    }

    private Flux<Release> fetchMissingReleasesFromDiscogs(List<Release> results) {
        return Flux.fromIterable(results)
                .flatMap(simpleRelease ->
                        exists(simpleRelease.getId())
                                .flatMap(exists ->
                                        Boolean.TRUE.equals(exists)
                                                ? Mono.just(simpleRelease)
                                                : fetchAndStoreFromDiscogs(simpleRelease.getId())
                                )
                );
    }
}
