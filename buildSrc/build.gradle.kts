plugins {
  `kotlin-dsl`
}

repositories {
  mavenLocal()
  mavenCentral()
  gradlePluginPortal()
  jcenter()
  maven("https://repo.spongepowered.org/maven")
  maven("https://dl.bintray.com/lanternpowered/maven")
}

dependencies {
  implementation(group = "org.spongepowered", name = "spongegradle", version = "0.11.0-SNAPSHOT")
  implementation(group = "org.lanternpowered", name = "lanterngradle", version = "1.0.2")
}
