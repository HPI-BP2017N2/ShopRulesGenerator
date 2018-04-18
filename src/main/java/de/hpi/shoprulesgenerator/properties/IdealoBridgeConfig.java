package de.hpi.shoprulesgenerator.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("idealobridge")
@Getter
@Setter
@Primary
public class IdealoBridgeConfig {

    private int maxOffers;
    private String oAuth2ClientId;
    private String oAuth2ClientSecret;
    private String accessTokenURI;
    private String apiUrl;
    private String sampleOffersRoute;
}