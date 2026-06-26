pluginManagement {
    val moddevVersion = providers.gradleProperty("moddev_version")

    plugins {
        id("net.neoforged.moddev") version moddevVersion
        id("org.shsts.checksource") version "0.1.0"
    }

    repositories {
        maven {
            name = "shsts"
            url = uri("https://www.shsts.org/m2")
        }
        gradlePluginPortal()
        mavenCentral()
        maven {
            name = "NeoForged"
            url = uri("https://maven.neoforged.net/releases")
            content {
                includeGroup("net.neoforged")
                includeGroup("net.neoforged.moddev")
            }
        }
        maven {
            name = "ParchmentMC"
            url = uri("https://maven.parchmentmc.org")
        }
    }
}

rootProject.name = "TinyCoreLib"

include("core")
include("datagen")
include("test")
include("test-datagen")
