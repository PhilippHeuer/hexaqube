pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
		id("io.quarkus") version "2.16.4.Final"
    }
}

rootProject.name = "hexaqube"
include(
	// core
    ":core",

	// platform
	":platform-discord4j",

	// qubes (features)
    ":qube-faq",
    ":qube-moderation",
    ":qube-audit",

    // all in one
	":bot",
)
