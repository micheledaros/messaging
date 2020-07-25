package com.micheledaros.messaging.infrastructure

import liquibase.integration.spring.SpringLiquibase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class LiquibaseConfiguration {

    @Bean
    fun liquibase(dataSource: DataSource): SpringLiquibase? {
        val liquibase = SpringLiquibase()
        liquibase.changeLog = "classpath:db/changelog/liquibase-changelog.xml"
        liquibase.dataSource = dataSource
        return liquibase
    }

}