plugins {
    kotlin("jvm")
    id("io.quarkus")
}

dependencies {
    // quarkus
    api("io.quarkus:quarkus-config-yaml")
    api("io.quarkus:quarkus-arc")
    api("io.quarkus:quarkus-resteasy-jackson")
    api("io.quarkus:quarkus-resteasy")
    api("io.quarkus:quarkus-smallrye-fault-tolerance")
    testImplementation("io.quarkus:quarkus-junit5")

    // jackson
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")

    // database
    api("io.quarkus:quarkus-hibernate-reactive")
    api("io.quarkus:quarkus-hibernate-reactive-panache-kotlin")
    api("io.quarkus:quarkus-reactive-pg-client")

    // reactive messaging
    api("io.cloudevents:cloudevents-kafka:2.4.2")
    api("io.quarkus:quarkus-smallrye-reactive-messaging")
    api("io.quarkus:quarkus-smallrye-reactive-messaging-kafka")

    // observability
    api("io.quarkus:quarkus-micrometer-registry-prometheus")
    api("io.quarkus:quarkus-smallrye-health")
    api("io.quarkus:quarkus-smallrye-openapi")

    // logging
    api("io.github.oshai:kotlin-logging-jvm")
    api("org.jboss.slf4j:slf4j-jboss-logmanager")

    // kotlin
    api("io.quarkus:quarkus-kotlin")
    api("org.jetbrains.kotlin:kotlin-stdlib")

    // okhttp3
    api("com.squareup.okhttp3:okhttp:4.10.0")
}
