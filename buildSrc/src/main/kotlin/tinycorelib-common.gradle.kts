import org.gradle.jvm.tasks.Jar

plugins {
    checkstyle
    eclipse
    id("net.neoforged.moddev")
    id("org.shsts.checksource")
    `maven-publish`
}

group = "org.shsts.tinycorelib"
version = "${property("minecraft_version")}-${property("mod_version")}"

neoForge {
    version = property("neo_version").toString()
    parchment {
        minecraftVersion = property("parchment_minecraft_version").toString()
        mappingsVersion = property("parchment_version").toString()
    }

    unitTest {
        enable()
    }

    runs {
        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            systemProperty("forge.logging.console.level", "debug")
            jvmArgument("-ea")
            disableIdeRun()
        }
    }
}

checkstyle {
    toolVersion = property("checkstyle_version").toString()
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val modMetadataProperties = mapOf(
    "mod_version" to version,
)

val generateModMetadata by tasks.registering(ProcessResources::class) {
    inputs.properties(modMetadataProperties)
    expand(modMetadataProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

val generateTestMetadata by tasks.registering(ProcessResources::class) {
    inputs.properties(modMetadataProperties)
    expand(modMetadataProperties)
    from("src/test/templates")
    into(layout.buildDirectory.dir("generated/sources/testMetadata"))
}

sourceSets.main {
    resources.srcDir(generateModMetadata)
}

sourceSets.test {
    resources.srcDir(generateTestMetadata)
}

neoForge.ideSyncTask(generateModMetadata)
neoForge.ideSyncTask(generateTestMetadata)

tasks.test {
    failOnNoDiscoveredTests = false
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(tasks.classes)
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}

tasks.build {
    dependsOn(sourcesJar)
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
