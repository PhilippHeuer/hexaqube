plugins {
	kotlin("jvm")
	kotlin("plugin.allopen")
}

dependencies {
    // quarkus
    testImplementation("io.quarkus:quarkus-junit5")

    // module
    api(project(":core"))

    // parser
    api("com.github.javaparser:javaparser-core")
    api("com.github.javaparser:javaparser-symbol-solver-core")

    // git
    api("org.eclipse.jgit:org.eclipse.jgit")

    // semver
    api("com.vdurmont:semver4j")
}
