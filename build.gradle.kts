plugins {
    kotlin("jvm") version "2.0.20"
    id("maven-publish")
    id("io.spring.dependency-management") version "1.1.3"
}

group = "com.salus"
version = "1.2-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.kafka:spring-kafka:3.0.7")
    implementation("org.springframework.boot:spring-boot-starter-web:3.0.7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.slf4j:slf4j-api:2.0.9")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.0.7")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.7") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.mockk:mockk:1.13.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "library-name"
        }
    }

    repositories {
        mavenLocal()
    }
}
