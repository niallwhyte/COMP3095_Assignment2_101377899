plugins {
    java
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.testcontainers:testcontainers-bom:1.18.1")
    testImplementation("org.testcontainers:mongodb:1.18.1")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.5")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.3")

    implementation("io.micrometer:micrometer-observation:1.11.3")
    implementation("io.micrometer:micrometer-tracing-bridge-brave:1.1.4")
//	zipkin
    implementation("io.zipkin.reporter2:zipkin-reporter-brave:2.16.4")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j:3.0.3")

}

tasks.withType<Test> {
    useJUnitPlatform()
}
