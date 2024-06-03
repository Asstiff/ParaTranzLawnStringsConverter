import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.mastiff"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.desktop.windows_x64)
    implementation("org.json:json:20231013")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.5.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe)
            packageName = "ParaTranzConverter"
            packageVersion = "1.0.1"

            macOS {
                iconFile.set(project.file("src/main/resources/images/icon.icns"))
            }
            windows {
                iconFile.set(project.file("src/main/resources/images/favicon.ico"))
                shortcut = true
            }
        }
    }
}
