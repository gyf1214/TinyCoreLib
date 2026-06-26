import org.gradle.jvm.tasks.Jar

base {
    archivesName = "tinycorelib"
}

checkSource {
    topPackage("org.shsts.tinycorelib")
    banImport("api", "content")
}

configurations {
    maybeCreate("api")
}

neoForge {
    mods {
        create("tinycorelib") {
            sourceSet(sourceSets.main.get())
        }
    }
}

val apiJar by tasks.registering(Jar::class) {
    archiveClassifier = "api"
    include("org/shsts/tinycorelib/api/**")
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
