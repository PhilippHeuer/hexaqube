plugins {
	kotlin("jvm")
	id("io.quarkus")
}

dependencies {
	// quarkus
	implementation("io.quarkus:quarkus-config-yaml")
	implementation("io.quarkus:quarkus-arc")
    testImplementation("io.quarkus:quarkus-junit5")

    // module
    api(project(":core"))
    api(project(":qube-faq"))
    api(project(":qube-audit"))
    api(project(":qube-moderation"))
    api(project(":qube-sourcecode"))
    api(project(":platform-discord4j"))

    // observability
	implementation("io.quarkus:quarkus-micrometer-registry-prometheus")
	implementation("io.quarkus:quarkus-smallrye-health")
	implementation("io.quarkus:quarkus-smallrye-openapi")

	// logging
    api("org.jboss.slf4j:slf4j-jboss-logmanager")

	// kotlin
	implementation("io.quarkus:quarkus-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

val mergePropertiesTask = tasks.register("mergeProperties") {
    dependsOn(subprojects)
    mustRunAfter("processResources")
    doLast {
        val files = rootProject.subprojects.map { p -> p.file("src/main/resources/application.properties") }
            .filter { it.exists() }
            .toList()

        val mergedProperties = files.map { it.readText() }
            .map { it.split("\n") }
            .flatMap { it.toList() }
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .joinToString(separator = "\n")
        project.buildDir.resolve("resources/main/application.properties").writeText(mergedProperties)
    }
}

tasks.named("classes") {
    dependsOn(mergePropertiesTask)
}
