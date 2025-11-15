package com.example.rateyourrecord.repository;

import com.example.rateyourrecord.model.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, String> {
    List<Release> findByTitleContainingIgnoreCase(String title);
    List<Release> findByGenresContainingIgnoreCase(String genre);
}
