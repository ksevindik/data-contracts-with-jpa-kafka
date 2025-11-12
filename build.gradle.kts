import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
	kotlin("plugin.jpa") version "1.8.22"
	id("com.google.protobuf") version "0.8.18"
	kotlin("plugin.allopen") version "1.8.22"
	kotlin("plugin.noarg") version "1.8.22"
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.kafka:spring-kafka")
	testImplementation("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")

	implementation("com.google.protobuf:protobuf-java:3.19.3")
	implementation("com.google.protobuf:protobuf-java-util:3.19.3")

	testImplementation("org.mockito:mockito-inline:2.13.0")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
}

sourceSets {
	main {
		proto {
		}
		java {
			setSrcDirs(
				listOf(
					"build/generated/source/proto/main/grpckt",
					"build/generated/source/proto/main/grpc",
					"build/generated/source/proto/main/java"
				)
			)
		}
	}
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.17.0:osx-x86_64"
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
