import dev.slne.surf.api.gradle.util.registerRequired
import dev.slne.surf.api.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
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

    authors.addAll("MikeyLLP", "Timonso", "TheBjoRedCraft")

    serverDependencies {
        registerRequired("surf-bitmap-provider-paper")
    }

    runServer {
        withSurfApiBukkit()
    }
}