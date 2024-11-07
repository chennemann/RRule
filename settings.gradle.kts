pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    versionCatalogs.create("libs").from(files("versions.toml"))
    repositories {
        mavenCentral()
    }
}

rootProject.name = "rrule"

