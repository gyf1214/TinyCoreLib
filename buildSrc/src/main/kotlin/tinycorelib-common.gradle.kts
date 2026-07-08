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

sourceSets.main {
    resources.srcDir(generateModMetadata)
}

neoForge.ideSyncTask(generateModMetadata)

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
