plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
}

dependencies {
    // quarkus
    testImplementation("io.quarkus:quarkus-junit5")

    // module
    api(project(":core"))
}
