package com.example.rateyourrecord.controller;

import com.example.rateyourrecord.dto.GenreCountDto;
import com.example.rateyourrecord.service.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/genres")
    public Mono<ResponseEntity<List<GenreCountDto>>> getGenresWithCounts() {
        return genreService.getGenreCounts()
                .map(ResponseEntity::ok);
    }
}
