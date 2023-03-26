plugins {
	kotlin("jvm")
	kotlin("plugin.allopen")
}

dependencies {
    // module
    api(project(":core"))
}
