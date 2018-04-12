package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.properties.ShopRulesGeneratorConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Getter(AccessLevel.PRIVATE)
@Component
@RequiredArgsConstructor
public class URLCleaner {

    private final RestTemplate restTemplate;

    private final ShopRulesGeneratorConfig config;

    String cleanURL(String dirtyUrl, long shopID) {
        return getRestTemplate().getForObject(getCleanURLURI(dirtyUrl, shopID), String.class);
    }

    private URI getCleanURLURI(String dirtyURL, long shopID) {
        return UriComponentsBuilder.fromUriString(getConfig().getUrlCleanerRoot())
                .path(getConfig().getCleanUrlRoute() + shopID)
                .queryParam("url", dirtyURL)
                .build()
                .encode()
                .toUri();
    }


}
