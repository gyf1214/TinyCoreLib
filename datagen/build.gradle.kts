import org.gradle.jvm.tasks.Jar

plugins {
    id("org.shsts.tinycorelib.common-conventions")
}

base {
    archivesName = "tinydatagen"
}

checkSource {
    topPackage("org.shsts.tinycorelib.datagen")
    banImport("api", "content")
}

configurations {
    maybeCreate("api")
}

neoForge {
    mods {
        create("tinydatagen") {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":core").sourceSets.main.get())
        }
    }
}

val apiJar by tasks.registering(Jar::class) {
    archiveClassifier = "api"
    include("org/shsts/tinycorelib/datagen/api/**")
    from(sourceSets.main.get().output)
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(tasks.classes)
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}

artifacts {
    add("api", apiJar)
}

dependencies {
    compileOnly(project(path = ":core"))
}

tasks.build {
    dependsOn(apiJar, sourcesJar)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.jar)
            artifact(apiJar)
            artifact(sourcesJar)
        }
    }
}
