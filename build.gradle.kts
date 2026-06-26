import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.api.publish.PublishingExtension

plugins {
    id("net.neoforged.moddev") apply false
    id("org.shsts.checksource") apply false
}

subprojects {
    group = "org.shsts.tinycorelib"
    version = "${property("minecraft_version")}-${property("mod_version")}"

    apply(plugin = "checkstyle")
    apply(plugin = "eclipse")
    apply(plugin = "net.neoforged.moddev")
    apply(plugin = "org.shsts.checksource")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension>("java") {
        toolchain.languageVersion = JavaLanguageVersion.of(21)
    }

    extensions.configure<CheckstyleExtension>("checkstyle") {
        toolVersion = property("checkstyle_version").toString()
    }

    extensions.configure<NeoForgeExtension>("neoForge") {
        setVersion(property("neo_version").toString())
        parchment {
            minecraftVersion.set(property("parchment_minecraft_version").toString())
            mappingsVersion.set(property("parchment_version").toString())
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

    extensions.configure<PublishingExtension>("publishing") {
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
}

println(
    "Java: ${System.getProperty("java.version")}, " +
        "JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}), " +
        "Arch: ${System.getProperty("os.arch")}",
)
println(
    "Minecraft: ${property("minecraft_version")}, " +
        "NeoForge: ${property("neo_version")}, " +
        "ModDevGradle: ${property("moddev_version")}",
)
