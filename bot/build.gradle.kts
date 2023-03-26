plugins {
	kotlin("jvm")
	id("io.quarkus")
}

dependencies {
	// quarkus
	implementation("io.quarkus:quarkus-config-yaml")
	implementation("io.quarkus:quarkus-arc")
	implementation("io.quarkus:quarkus-resteasy-jackson")
	implementation("io.quarkus:quarkus-resteasy")
    testImplementation("io.quarkus:quarkus-junit5")

    // module
    api(project(":core"))
    api(project(":qube-faq"))
    api(project(":qube-audit"))
    api(project(":qube-moderation"))
    api(project(":platform-discord4j"))

    // database
    implementation("io.quarkus:quarkus-hibernate-reactive")
    implementation("io.quarkus:quarkus-reactive-pg-client")
    implementation("io.quarkus:quarkus-hibernate-reactive-panache-kotlin")

    // observability
	implementation("io.quarkus:quarkus-micrometer-registry-prometheus")
	implementation("io.quarkus:quarkus-smallrye-health")
	implementation("io.quarkus:quarkus-smallrye-openapi")

	// logging
	implementation("org.jboss.slf4j:slf4j-jboss-logmanager")

	// kotlin
	implementation("io.quarkus:quarkus-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
}
