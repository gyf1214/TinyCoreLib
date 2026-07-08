import org.gradle.jvm.tasks.Jar

plugins {
    id("tinycorelib-common")
}

evaluationDependsOn(":core")

neoForge {
    mods {
        create("tinycorelib") {
            sourceSet(project(":core").sourceSets.main.get())
        }

        create("tinycorelib_test") {
            sourceSet(project(":core").sourceSets.test.get())
        }

        create("tinydatagen") {
            sourceSet(sourceSets.main.get())
        }

        create("tinydatagen_test") {
            sourceSet(sourceSets.test.get())
        }
    }

    runs {
        create("data") {
            data()
            gameDirectory = rootProject.file("run/data")

            programArguments.addAll(
                "--mod",
                "tinydatagen_test",
                "--all",
                "--output",
                rootProject.project(":core").file("src/generated/resources/").absolutePath,
            )
            programArguments.addAll(
                "--existing",
                rootProject.project(":core").file("src/test/resources/").absolutePath,
            )
            programArguments.addAll(
                "--existing",
                file("src/test/resources/").absolutePath,
            )
        }
    }
}

dependencies {
    compileOnly(project(":core"))

    testImplementation(project(":core"))
    testImplementation(rootProject.project(":core").sourceSets.test.get().output)
}

checkSource {
    topPackage("org.shsts.tinycorelib.datagen")
    banImport("api", "content")
    includeTest()
}

configurations {
    maybeCreate("api")
}

val apiJar by tasks.registering(Jar::class) {
    archiveClassifier = "api"
    include("org/shsts/tinycorelib/datagen/api/**")
    from(sourceSets.main.get().output)
}

artifacts {
    add("api", apiJar)
}

tasks.build {
    dependsOn(apiJar)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.jar)
            artifact(apiJar)
            artifact(tasks.sourcesJar)
        }
    }
}
