package com.micheledaros.messaging.infrastructure

import com.micheledaros.messaging.user.domain.CurrentRestUserIdProvider.Companion.USER_ID_HEADER
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.schema.ModelRef
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 * Swagger UI is available at http://localhost:8080/swagger-ui.html
 */
@Configuration
@Profile("development")
@EnableSwagger2
class SwaggerConfig() {

    @Bean
    fun productApi(): Docket {

        val additionalHeaders = listOf(
                ParameterBuilder()
                    .name(USER_ID_HEADER)
                    .modelRef(ModelRef("String"))
                    .parameterType("header")
                    .required(false)
                    .build())

        return Docket(DocumentationType.SWAGGER_2)
                .globalOperationParameters(additionalHeaders)
                .select().apis(RequestHandlerSelectors.basePackage("com.micheledaros.messaging"))
                .build()
    }
}
