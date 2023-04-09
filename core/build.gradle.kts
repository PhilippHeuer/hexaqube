plugins {
    kotlin("jvm")
    id("io.quarkus")
}

dependencies {
    // quarkus
    api("io.quarkus:quarkus-config-yaml")
    api("io.quarkus:quarkus-arc")
    api("io.quarkus:quarkus-smallrye-fault-tolerance")
    api("io.quarkus:quarkus-scheduler")
    api("io.quarkus:quarkus-cache")
    testImplementation("io.quarkus:quarkus-junit5")

    // database
    api("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    api("io.quarkus:quarkus-hibernate-search-orm-elasticsearch")
    api("io.quarkus:quarkus-jdbc-postgresql")
    //api("io.quarkus:quarkus-hibernate-reactive")
    //api("io.quarkus:quarkus-hibernate-reactive-panache-kotlin")
    //api("io.quarkus:quarkus-reactive-pg-client")
    api("com.vladmihalcea:hibernate-types-60:2.21.1")

    // jackson
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // reactive messaging
    api("io.cloudevents:cloudevents-kafka")
    api("io.quarkus:quarkus-smallrye-reactive-messaging")
    api("io.quarkus:quarkus-smallrye-reactive-messaging-kafka")

    // observability
    api("io.quarkus:quarkus-micrometer-registry-prometheus")
    api("io.quarkus:quarkus-smallrye-health")
    api("io.quarkus:quarkus-smallrye-openapi")

    // distributed locking
    api("net.javacrumbs.shedlock:shedlock-provider-etcd-jetcd")
    api("net.javacrumbs.shedlock:shedlock-provider-inmemory")

    // logging
    api("io.github.oshai:kotlin-logging-jvm")
    api("org.jboss.slf4j:slf4j-jboss-logmanager")

    // kotlin
    api("io.quarkus:quarkus-kotlin")
    api("org.jetbrains.kotlin:kotlin-stdlib")

    // okhttp3
    api("com.squareup.okhttp3:okhttp:4.10.0")

    // template engine
    api("com.github.jknack:handlebars")
}
