import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

configurations {
    create("common") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    compileClasspath { extendsFrom(configurations["common"]) }
    runtimeClasspath { extendsFrom(configurations["common"]) }
    named("developmentFabric") { extendsFrom(configurations["common"]) }

    // Files in this configuration will be bundled into your mod using the Shadow plugin.
    // Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
    create("shadowBundle") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

dependencies {
    implementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    implementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")

    // Architectury API. This is optional, and you can comment it out if you don't need it.
    implementation("dev.architectury:architectury-fabric:${rootProject.property("architectury_api_version")}")

    add("common", project(":common"))
    add("common", project(mapOf("path" to ":common", "configuration" to "standaloneEventRuntime")))
    add("shadowBundle", project(mapOf("path" to ":common", "configuration" to "transformProductionFabric")))
    add("shadowBundle", project(mapOf("path" to ":common", "configuration" to "standaloneEventRuntime")))
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to inputs.properties["version"]))
    }
}

// Minecraft is not obfuscated on this version, so there is no remapJar task.
// The shaded jar is the mod jar that ships.
tasks.named<Jar>("jar") {
    archiveClassifier.set("dev")
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations["shadowBundle"])
    archiveClassifier.set(null)
}

tasks.named("assemble") {
    dependsOn(tasks.named("shadowJar"))
}

configurations {
    named("apiElements") {
        outgoing.artifacts.clear()
        outgoing.artifact(tasks.named<ShadowJar>("shadowJar"))
    }
    named("runtimeElements") {
        outgoing.artifacts.clear()
        outgoing.artifact(tasks.named<ShadowJar>("shadowJar"))
    }
}
