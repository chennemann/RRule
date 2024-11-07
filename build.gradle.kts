plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

group = "de.chennemann.rrule"
version = "1.0.0"

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            // KotlinX
            api(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}