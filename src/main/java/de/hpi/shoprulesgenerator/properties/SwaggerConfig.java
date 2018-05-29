package de.hpi.shoprulesgenerator.properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("de.hpi.shoprulesgenerator.api"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData());
    }
    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Shop Rules Generator API")
                .description("Spring REST API to get shop specific rules that can be used to extract product attributes from HTML")
                .version("1.0.0")
                .contact(new Contact("Hasso-Plattner Institute, Information Systems Group", "https://hpi.de/naumann/teaching/bachelorprojekte/inventory-management.html", "office-naumann@hpi.de"))
                .build();
    }
}