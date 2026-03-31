import dev.slne.surf.surfapi.gradle.util.registerRequired
import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin") version "1.21.11+"
}

group = "dev.slne.surf.moderation.tools"
version = findProperty("version") as String


dependencies {
    compileOnly(libs.surf.bitmap)
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.moderation.tools.PaperMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    authors.addAll("MikeyLLP", "Timonso", "red")

    serverDependencies {
        registerRequired("surf-bitmap-provider-paper")
    }

    runServer {
    }
}