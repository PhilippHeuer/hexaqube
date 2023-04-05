plugins {
	kotlin("jvm") version "1.8.20"
	kotlin("plugin.allopen") version "1.8.20"
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
		implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.0.0.CR1"))

        constraints {
            // logging
            api("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-27")
            api("org.jboss.slf4j:slf4j-jboss-logmanager:2.0.1.Final")

            // java parser
            api("com.github.javaparser:javaparser-core:3.25.2")
            api("com.github.javaparser:javaparser-symbol-solver-core:3.25.2")

            // git
            api("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r")

            // semver
            api("com.vdurmont:semver4j:3.1.0")
        }
    }

    configurations {
        all {
            resolutionStrategy {
                force("com.github.javaparser:javaparser-core:3.25.2")
            }
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
        compilerOptions {
            freeCompilerArgs.set(listOf(
                "-Xcontext-receivers",
            ))
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_8)
        }
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
