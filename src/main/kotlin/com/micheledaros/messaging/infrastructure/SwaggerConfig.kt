package com.micheledaros.messaging.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.net.URI

/**
 * Swagger UI is available at http://localhost:8080/swagger-ui.html
 */
@Configuration
@Profile("development")
@EnableSwagger2
class SwaggerConfig() {
    @Bean
    fun productApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.basePackage("com.micheledaros.messaging"))
                .build()
    }
}
