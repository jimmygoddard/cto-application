import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.5.2"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("com.google.cloud.tools.jib") version "3.1.1"
  kotlin("jvm") version "1.5.20"
  kotlin("plugin.spring") version "1.5.20"
  kotlin("plugin.jpa") version "1.5.20"
}

group = "com.cogitocorp"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

extra["testcontainersVersion"] = "1.15.3"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("io.github.microutils:kotlin-logging-jvm:+")
  implementation("org.springdoc:springdoc-openapi-ui:+")
  implementation("org.springdoc:springdoc-openapi-webmvc-core:+")
  runtimeOnly("org.postgresql:postgresql")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:postgresql")
}

dependencyManagement {
  imports {
    mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
  }
}

dependencyLocking {
  lockAllConfigurations()
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.register("resolveAndLockAll") {
  doFirst {
    require(gradle.startParameter.isWriteDependencyLocks)
  }
  doLast {
    configurations.filter {
      // Add any custom filtering on the configurations to be resolved
      it.isCanBeResolved
    }.forEach { it.resolve() }
  }
}

val imageTag by extra {
  val builder = StringBuilder(project.version.toString())
  if (project.hasProperty("buildNum")) {
    builder.append("-b").append(property("buildNum"))
  }
  if (project.hasProperty("gitSha")) {
    builder.append("-").append(property("gitSha"))
  }
  builder.toString()
}

// not working
jib {
  setAllowInsecureRegistries(true)
  to {
    image = "file-service"
    tags = setOf(imageTag, "latest")
  }
  from {
    image = "adoptopenjdk:11-jre"
  }
  container {
    mainClass = "com.cogitocorp.service.file.FileServiceApplication"
    creationTime = "USE_CURRENT_TIMESTAMP"
    user = "nobody"
  }
}
