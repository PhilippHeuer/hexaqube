plugins {
	kotlin("jvm")
	kotlin("plugin.allopen")
}

dependencies {
	// quarkus
	api("io.quarkus:quarkus-core")
	api("io.quarkus:quarkus-smallrye-health")

	// module
	api(project(":core"))

	// micrometer
	api("io.micrometer:micrometer-core")

	// discord4j
	api("com.discord4j:discord4j-core")
}
