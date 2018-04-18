package de.hpi.shoprulesgenerator.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IdealoBridgeConfig clientProperties;

    @Bean
    public RestTemplate oAuthRestTemplate() {
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setId("1");
        resourceDetails.setClientId(getClientProperties().getOAuth2ClientId());
        resourceDetails.setClientSecret(getClientProperties().getOAuth2ClientSecret());
        resourceDetails.setAccessTokenUri(getClientProperties().getAccessTokenURI());
        return new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext());
    }
}
