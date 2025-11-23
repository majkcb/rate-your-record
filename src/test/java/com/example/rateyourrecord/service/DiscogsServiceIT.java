package com.example.rateyourrecord.service;

import com.example.rateyourrecord.client.DiscogsClient;
import com.example.rateyourrecord.model.DiscogsResult;
import com.example.rateyourrecord.model.Release;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DiscogsServiceIT {

    @Autowired
    private DiscogsService discogsService;

    @MockitoBean
    private DiscogsClient discogsClient;

    @Test
    void whenSearchReleases_thenReturnMappedReleases() {
        // Arrange
        DiscogsResult mockResult = new DiscogsResult(
                123,
                "Test Title",
                1990,
                List.of("Rock", "Prog Rock"),
                List.of("Rock", "Prog Rock"),
                "Sweden",
                "https://example.com/cover.jpg",
                "Test Artist"

        );

        when(discogsClient.searchReleases(anyString()))
                .thenReturn(Mono.just(List.of(mockResult)));

        // Act
        Mono<List<Release>> resultMono = discogsService.searchReleases("test query");

        // Assert
        StepVerifier.create(resultMono)
                .assertNext(releases -> {
                    Release r = releases.getFirst();
                    assert releases.size() == 1;
                    assert r.getTitle().equals("Test Title");
                    assert r.getArtist().equals("Test Artist");
                    assert r.getReleaseYear().equals("2023");
                    assert r.getGenres().contains("Rock");
                    assert r.getGenres().contains("Prog Rock");
                })
                .verifyComplete();
    }
}
