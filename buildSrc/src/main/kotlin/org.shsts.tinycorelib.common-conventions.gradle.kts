import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    checkstyle
    eclipse
    id("net.neoforged.moddev")
    id("org.shsts.checksource")
    `maven-publish`
}

group = "org.shsts.tinycorelib"
version = "${property("minecraft_version")}-${property("mod_version")}"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

checkstyle {
    toolVersion = property("checkstyle_version").toString()
}

neoForge {
    version = property("neo_version").toString()
    parchment {
        minecraftVersion = property("parchment_minecraft_version").toString()
        mappingsVersion = property("parchment_version").toString()
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 21
}

fun ProcessResources.expandModProperties() {
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(
            project.properties + mapOf(
                "mod_version" to version,
                "minecraft_version_range" to property("minecraft_version_range"),
                "neo_version_range" to property("neo_version_range"),
            ),
        )
    }
}

tasks.named<ProcessResources>("processResources") {
    expandModProperties()
}

tasks.matching { it.name == "processTestResources" }.configureEach {
    (this as ProcessResources).expandModProperties()
}

publishing {
    repositories {
        if (project.findProperty("shstsUser") != null) {
            maven {
                name = "shsts"
                url = uri("https://www.shsts.org/m2")
                credentials {
                    username = project.findProperty("shstsUser").toString()
                    password = project.findProperty("shstsPassword")?.toString() ?: ""
                }
            }
        }
    }
}
