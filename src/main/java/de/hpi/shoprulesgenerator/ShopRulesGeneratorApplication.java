package de.hpi.shoprulesgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ShopRulesGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopRulesGeneratorApplication.class, args);
    }
}
