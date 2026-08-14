// KMP : Desktop + WasmJs

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

group = "com.neojou.humanformationcalculator"
version = "0.4.0"

kotlin {
    jvm("desktop")
    jvmToolchain(25)

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("HumanFormationCalculator")
        browser { }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val wasmJsMain by getting
    }
}

compose.desktop {
    application {
        mainClass = "com.neojou.humanformationcalculator.MainKt"
    }
}

compose.resources {
    packageOfResClass = "com.neojou.humanformationcalculator"
}
