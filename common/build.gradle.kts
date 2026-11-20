architectury {
    common(rootProject.property("enabled_platforms").toString().split(","))
}

// The upstream API and provider share a Java package and must enter JPMS as one module.
val standaloneEventBundle = configurations.create("standaloneEventBundle") {
    isCanBeConsumed = false
    isCanBeResolved = true
}
configurations.compileOnly { extendsFrom(standaloneEventBundle) }

dependencies {
    // We depend on Fabric Loader here to use the Fabric @Environment annotations,
    // which get remapped to the correct annotations on each platform.
    // Do NOT use other classes from Fabric Loader.
    compileOnly("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")

    // Architectury API. This is optional, and you can comment it out if you don't need it.
    // compileOnly("dev.architectury:architectury:${rootProject.property("architectury_api_version")}")

    standaloneEventBundle("io.github.vlouboos:standaloneevent-common:1.7")
    implementation("org.json:json:20260814")
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
}

val standaloneEventRuntime = configurations.create("standaloneEventRuntime") {
    isCanBeConsumed = true
    isCanBeResolved = false
}

val standaloneEventJar = tasks.register<Jar>("standaloneEventJar") {
    archiveClassifier.set("standaloneevent")
    from({ standaloneEventBundle.map { zipTree(it) } })
    exclude("META-INF/MANIFEST.MF")
    duplicatesStrategy = DuplicatesStrategy.FAIL
}

artifacts {
    add(standaloneEventRuntime.name, standaloneEventJar)
}
