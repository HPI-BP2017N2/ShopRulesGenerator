package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.properties.IdealoBridgeConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Component
public class IdealoBridge {

    private final RestTemplate oAuthRestTemplate;

    private final IdealoBridgeConfig properties;

    IdealoOffers getSampleOffers(long shopID) {
        return getOAuthRestTemplate().getForObject(getSampleOffersURI(shopID), IdealoOffers.class);
    }

    private URI getSampleOffersURI(long shopID) {
        return UriComponentsBuilder.fromUriString(getProperties().getApiUrl())
                .path(getProperties().getSampleOffersRoute() + shopID)
                .queryParam("maxCount", getProperties().getMaxOffers())
                .build()
                .encode()
                .toUri();
    }
}
