pluginManagement {
    repositories {
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
        maven {
            name = "shsts"
            url = uri("https://www.shsts.org/m2")
        }
    }
}

rootProject.name = "TinyCoreLib"

include("core")
include("datagen")
include("test")
include("test-datagen")
