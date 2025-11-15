package com.example.rateyourrecord.service;

import com.example.rateyourrecord.dto.GenreCountDto;
import com.example.rateyourrecord.repository.ReleaseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final ReleaseRepository releaseRepository;

    public GenreService(ReleaseRepository releaseRepository) {
        this.releaseRepository = releaseRepository;
    }

    public Mono<List<GenreCountDto>> getGenreCounts() {
        return Mono.fromCallable(releaseRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .map(releases -> releases.stream()
                        .flatMap(release -> release.getGenres().stream())
                        .collect(Collectors.groupingBy(genre -> genre, Collectors.counting())))
                .map(genreCounts -> genreCounts.entrySet().stream()
                        .map(entry -> new GenreCountDto(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList()));
    }
}
