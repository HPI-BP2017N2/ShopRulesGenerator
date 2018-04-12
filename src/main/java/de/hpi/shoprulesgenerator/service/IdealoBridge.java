package de.hpi.shoprulesgenerator.service;

import de.hpi.shoprulesgenerator.properties.IdealoBridgeProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Component
public class IdealoBridge {

    private final RestTemplate oAuthRestTemplate;

    private final IdealoBridgeProperties properties;

    public List<IdealoOffer> getSampleOffers(long shopID) {
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
