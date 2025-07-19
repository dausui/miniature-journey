// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.4" apply false
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    
    detekt {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom("$projectDir/config/detekt/detekt.yml")
        baseline = file("$projectDir/config/detekt/baseline.xml")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}