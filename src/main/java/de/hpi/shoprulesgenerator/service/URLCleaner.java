package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.dto.SuccessCleanResponse;
import de.hpi.shoprulesgenerator.properties.ShopRulesGeneratorConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Getter(AccessLevel.PRIVATE)
@Component
@RequiredArgsConstructor
public class URLCleaner {

    private final RestTemplate restTemplate;

    private final ShopRulesGeneratorConfig config;

    @Retryable(
            value = { HttpClientErrorException.class },
            backoff = @Backoff(delay = 3000, multiplier = 5))
    @Cacheable("shopRootUrls")
    public String cleanURL(String dirtyUrl, long shopID) {
        return getRestTemplate().getForObject(getCleanURLURI(dirtyUrl, shopID), SuccessCleanResponse.class).getData()
                .getUrl();
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
