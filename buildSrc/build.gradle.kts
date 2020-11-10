plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    jcenter()
    maven("https://repo-new.spongepowered.org/repository/maven-public/")
    maven("https://repo.spongepowered.org/maven")
    maven("https://dl.bintray.com/lanternpowered/maven")
}

dependencies {
    implementation(group = "org.spongepowered", name = "SpongeGradle", version = "0.11.3-SNAPSHOT")
    implementation(group = "org.lanternpowered", name = "lanterngradle", version = "1.0.2")
    implementation(group = "gradle.plugin.org.cadixdev.gradle", name = "licenser", version = "0.5.0")
}
