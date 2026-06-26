plugins {
    id("tinycorelib-common")
}

base {
    archivesName = "tinydatagen_test"
}

checkSource {
    topPackage("org.shsts.tinycorelib.test.datagen")
}

neoForge {
    runs {
        create("data") {
            data()
            systemProperty("forge.logging.markers", "REGISTRIES")
            systemProperty("forge.logging.console.level", "debug")
            jvmArgument("-ea")

            gameDirectory = project.file("run/data")
            programArguments.addAll(
                "--mod",
                "tinydatagen_test",
                "--all",
                "--output",
                rootProject.project(":test").file("src/generated/resources/").absolutePath,
            )
            programArguments.addAll(
                "--existing",
                rootProject.project(":test").file("src/main/resources/").absolutePath,
            )
            programArguments.addAll(
                "--existing",
                file("src/main/resources/").absolutePath,
            )
        }
    }

    mods {
        create("tinycorelib") {
            sourceSet(project(":core").sourceSets.main.get())
        }

        create("tinydatagen") {
            sourceSet(project(":datagen").sourceSets.main.get())
        }

        create("tinycorelib_test") {
            sourceSet(project(":test").sourceSets.main.get())
        }

        create("tinydatagen_test") {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    compileOnly(project(path = ":core"))
    compileOnly(project(path = ":datagen"))
    runtimeOnly(project(path = ":core"))
    runtimeOnly(project(path = ":datagen"))

    compileOnly(project(path = ":test"))
    runtimeOnly(project(path = ":test"))
}
