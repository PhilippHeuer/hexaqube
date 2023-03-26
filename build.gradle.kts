plugins {
	kotlin("jvm") version "1.8.10"
	kotlin("plugin.allopen") version "1.8.10"
    id("io.quarkus") apply(false)
}

allprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.allopen")

	repositories {
		mavenCentral()
	}

	dependencies {
		// BOM
		implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:2.16.4.Final"))

        constraints {
            // logging
            implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-23")
            implementation("org.jboss.slf4j:slf4j-jboss-logmanager:2.0.1.Final")
        }
    }

    java {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.compilerArgs.add("-parameters")
	}

	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
		kotlinOptions.javaParameters = true
	}

    tasks.withType<Test> {
        useJUnitPlatform()
        systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    }

    allOpen {
        annotation("javax.ws.rs.Path")
        annotation("javax.enterprise.context.ApplicationScoped")
        annotation("io.quarkus.test.junit.QuarkusTest")
    }
}
