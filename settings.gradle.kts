pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
		id("io.quarkus") version "3.0.0.CR2"
    }
}

rootProject.name = "hexaqube"
include(
	// core
    ":core",

	// platform
	":platform-discord4j",

	// qubes (features)
    ":qube-moderation",
    ":qube-audit",
    ":qube-codesearch",
    ":qube-docsearch",

    // all in one
	":bot",
)
