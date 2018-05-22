package de.hpi.shoprulesgenerator.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Component
@EnableRetry
@EnableAsync
@EnableConfigurationProperties
@ConfigurationProperties("shoprulesgenerator-settings")
@Getter @Setter
public class ShopRulesGeneratorConfig {

    @Min(0)
    private double scoreThreshold;

    @Min(0)
    private int fetchDelay;

    @NotBlank
    private String userAgent;

    @NotBlank
    private String urlCleanerRoot;

    @NotBlank
    private String cleanUrlRoute;

}
