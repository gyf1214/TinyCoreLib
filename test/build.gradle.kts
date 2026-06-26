plugins {
    id("org.shsts.tinycorelib.common-conventions")
}

base {
    archivesName = "tinycorelib_test"
}

checkSource {
    topPackage("org.shsts.tinycorelib.test")
}

neoForge {
    runs {
        create("client") {
            client()
            gameDirectory = project.file("run/client")

            systemProperty("forge.logging.markers", "REGISTRIES")
            systemProperty("forge.logging.console.level", "debug")
            jvmArgument("-ea")
        }

        create("server") {
            server()
            gameDirectory = project.file("run/server")

            systemProperty("forge.logging.markers", "REGISTRIES")
            systemProperty("forge.logging.console.level", "debug")
            jvmArgument("-ea")
        }

        create("gameTestServer") {
            type = "gameTestServer"
            gameDirectory = project.file("run/game-test-server")

            systemProperty("forge.logging.markers", "REGISTRIES")
            systemProperty("forge.logging.console.level", "debug")
            jvmArgument("-ea")
        }
    }

    mods {
        create("tinycorelib") {
            sourceSet(project(":core").sourceSets.main.get())
        }

        create("tinycorelib_test") {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources {
        srcDir("src/generated/resources")
        exclude(".cache/")
    }
}

dependencies {
    compileOnly(project(path = ":core"))
    runtimeOnly(project(path = ":core"))

    testCompileOnly(project(path = ":core"))
    testRuntimeOnly(project(path = ":core"))
    testCompileOnly(project(path = ":datagen"))
    testRuntimeOnly(project(path = ":datagen"))
}
