package com.example.rateyourrecord.config;

import com.example.rateyourrecord.model.DiscogsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient discogsWebClient(WebClient.Builder builder, DiscogsProperties discogsProperties) {
        return builder
                .baseUrl(discogsProperties.getBaseUrl())
                .defaultHeader("Authorization", "Discogs token=" + discogsProperties.getToken())
                .defaultHeader("User-Agent", discogsProperties.getUserAgent())
                .build();
    }
}
