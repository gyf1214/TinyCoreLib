import org.gradle.jvm.tasks.Jar

plugins {
    id("tinycorelib-common")
}

base {
    archivesName = "tinycorelib"
}

neoForge {
    mods {
        create("tinycorelib") {
            sourceSet(sourceSets.main.get())
        }

        create("tinycorelib_test") {
            sourceSet(sourceSets.test.get())
        }
    }

    runs {
        create("client") {
            client()
            gameDirectory = rootProject.file("run/client")
        }

        create("server") {
            server()
            gameDirectory = rootProject.file("run/server")
        }

        create("gameTestServer") {
            type = "gameTestServer"
            gameDirectory = rootProject.file("run/gameTestServer")
            systemProperty("tinycorelib.gameTest.defaultTemplate", "empty_1x1x1")
        }
    }
}

sourceSets.test {
    resources {
        srcDir("src/generated/resources")
        exclude(".cache/")
    }
}

checkSource {
    topPackage("org.shsts.tinycorelib")
    banImport("api", "content")
    includeTest()
}

configurations {
    maybeCreate("api")
}

val apiJar by tasks.registering(Jar::class) {
    archiveClassifier = "api"
    include("org/shsts/tinycorelib/api/**")
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
