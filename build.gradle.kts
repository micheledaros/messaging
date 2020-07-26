import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val sprinBootVersion = "2.3.2.RELEASE"
	val kotlinVersion = "1.3.72"

	id("org.springframework.boot") version sprinBootVersion
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
}

group = "com.micheledaros"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.liquibase:liquibase-core")

	runtimeOnly("org.postgresql:postgresql")

	implementation("com.h2database:h2")

	implementation("io.springfox:springfox-swagger2:2.9.2")
	implementation("io.springfox:springfox-swagger-ui:2.9.2")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.assertj:assertj-core:3.16.1")
	testImplementation("com.natpryce:make-it-easy:4.0.1")
	testImplementation("io.rest-assured:rest-assured:4.2.0")
	testImplementation("io.rest-assured:json-path:4.2.0")
	testImplementation("io.rest-assured:xml-path:4.2.0")
	testImplementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.11.1")


}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}
