package com.pbl.foundlost.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

//http://localhost:8080/foundlost/swagger-ui/
@Configuration
public class SwaggerConfig {

    private RequestParameter authorizationParameter() {
        RequestParameterBuilder tokenBuilder = new RequestParameterBuilder();
        tokenBuilder
                .name("Authorization")
                .description("access_token")
                .required(false)
                .in("header")
                .accepts(singleton(MediaType.APPLICATION_JSON))
                .build();
        return tokenBuilder.build();
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .description("My Reactive API")
                        .title("My Domain object API")
                        .version("1.0.0")
                        .build())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.pbl.foundlost.controllers"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(singletonList(authorizationParameter()));
    }
}
