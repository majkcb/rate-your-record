package com.example.rateyourrecord.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Release {
    @Id
    private String id;
    private String title;
    private String artist;
    private String coverImageUrl;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> tracklist;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> genres;
    private String releaseYear;
    private String totalTime;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double averageRating;
    private Long numberOfRatings;
    private LocalDateTime lastUpdated;

    public Release() {
        this.averageRating = 0.0;
        this.numberOfRatings = 0L;
        this.lastUpdated = LocalDateTime.now();
    }
}
