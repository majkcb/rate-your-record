package com.example.rateyourrecord.model;

import com.example.rateyourrecord.dto.DiscogsSearchResultDto;

import java.util.List;

public record DiscogsSearchResponse(
        List<DiscogsSearchResultDto> results
) {}
