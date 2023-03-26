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

	// openai
	implementation("com.theokanning.openai-gpt3-java:client:0.11.1")
}
