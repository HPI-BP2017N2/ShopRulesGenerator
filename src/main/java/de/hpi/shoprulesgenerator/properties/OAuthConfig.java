package de.hpi.shoprulesgenerator.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.client.RestTemplate;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@Configuration
@EnableOAuth2Client
@EnableConfigurationProperties(IdealoBridgeConfig.class)
public class OAuthConfig {

    @Bean
    public RestTemplate oAuthRestTemplate(IdealoBridgeConfig config) {
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setId("1");
        resourceDetails.setClientId(config.getOAuth2ClientId());
        resourceDetails.setClientSecret(config.getOAuth2ClientSecret());
        resourceDetails.setAccessTokenUri(config.getAccessTokenURI());
        return new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext());
    }
}
