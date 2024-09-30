@file:Suppress("UnstableApiUsage")

plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.22"
    id("dev.architectury.loom") version "1.7-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
    id("dev.kikugie.j52j") version "1.0.2"
}

val mod = ModData(project)
val loader = LoaderData(project, loom.platform.get().name.lowercase())
val minecraftVersion = MinecraftVersionData(stonecutter)
val awName = "allthetrims.accesswidener"

version = "${mod.version}-$loader+$minecraftVersion"
group = mod.group
base.archivesName.set(mod.name)

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") }
        filter { includeGroup("maven.modrinth") }
    }
    maven("https://cursemaven.com")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.ladysnake.org/releases")
    maven("https://maven.enjarai.dev/releases")
    maven("https://maven.shedaniel.me")
    maven("https://maven.blamejared.com/")
    maven("https://jitpack.io")
    maven("https://maven.bawnorton.com/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    modImplementation("dev.isxander:yet-another-config-lib:${property("yacl")}+$minecraftVersion-$loader")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-$loader:${property("rei")}")
    modCompileOnly("me.shedaniel.cloth:cloth-config-$loader:${property("cloth_config")}")

    modCompileOnly("dev.emi:emi-$loader:${property("emi")}+$minecraftVersion:api")
    modCompileOnly("dev.emi:emi-$loader:${property("emi")}+$minecraftVersion")

    modCompileOnly("mezz.jei:jei-$minecraftVersion-$loader-api:${property("jei")}") { isTransitive = false }
    modCompileOnly("mezz.jei:jei-$minecraftVersion-$loader:${property("jei")}") { isTransitive = false }

    modImplementation("maven.modrinth:modernfix:${property("modernfix")}")
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/$awName"))

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }

    runConfigs["client"].apply {
        vmArgs("-Dmixin.debug.export=true")
        programArgs("--username=Bawnorton")
    }
}

tasks {
    withType<JavaCompile> {
        options.release = minecraftVersion.javaVersion()
    }

    processResources {
        val refmap = "refmap" to "${mod.name}-$minecraftVersion-$loader-refmap.json"
        inputs.properties(refmap)

        filesMatching("allthetrims-compat.mixins.json5") {
            expand(refmap)
        }
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion())
    targetCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion())
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

stonecutter.debug(true)
if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }
}
stonecutter.dependency("java", minecraftVersion.javaVersion().toString())

StonecutterSwapper(stonecutter)
    .register("armour_material", "1.20.6", "ArmorMaterial", "RegistryEntry<ArmorMaterial>")
    .apply(minecraftVersion.toString())

if(loader.isFabric) {
    dependencies {
        modImplementation("net.fabricmc:fabric-loader:${loader.getVersion()}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api")}+$minecraftVersion")

        include(implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:${property("mixin_squared")}")!!)!!)

        modImplementation("com.terraformersmc:modmenu:${property("mod_menu")}")

        modCompileOnly("maven.modrinth:elytra-trims:${property("elytra_trims")}")
        modCompileOnly("maven.modrinth:iris:${property("iris")}+$minecraftVersion")
        modCompileOnly("maven.modrinth:show-me-your-skin:${property("show_me_your_skin")}")
        modCompileOnly("maven.modrinth:female-gender:${property("wildfire_gender")}")
        modCompileOnly("maven.modrinth:mythicmetals:${property("mythic_metals")}")
        modCompileOnly("maven.modrinth:bclib:${property("bclib")}")

        if(minecraftVersion.lessThan("1.21")) {
            modImplementation("maven.modrinth:immersive-armors:${property("immersive_armors")}+$minecraftVersion")
        }

        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
    }

    tasks {
        processResources {
            val modMetadata = mapOf(
                "version" to mod.version,
                "minecraft_dependency" to mod.minecraftDependency,
                "java" to minecraftVersion.javaVersion(),
            )

            inputs.properties(modMetadata)
            filesMatching("fabric.mod.json") { expand(modMetadata) }
        }
    }
}

if (loader.isNeoForge) {
    dependencies {
        neoForge("net.neoforged:neoforge:${loader.getVersion()}")

        compileOnly(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.2.0-beta.6")!!)
        implementation(include("com.github.bawnorton.mixinsquared:mixinsquared-forge:0.2.0-beta.6")!!)

        modCompileOnly("maven.modrinth:elytra-trims:${property("elytra_trims")}").stripAw(project)

        forgeRuntimeLibrary(runtimeOnly("org.quiltmc.parsers:json:${findProperty("quilt_parsers")}")!!)
        forgeRuntimeLibrary(runtimeOnly("org.quiltmc.parsers:gson:${findProperty("quilt_parsers")}")!!)
        modRuntimeOnly("curse.maven:advanced-netherite-495336:5427379")

        mappings(loom.layered {
            mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
            mappings("dev.architectury:yarn-mappings-patch-neoforge:1.21+build.4")
        })
    }

    tasks {
        processResources {
            val modMetadata = mapOf(
                "version" to mod.version,
                "minecraft_dependency" to mod.minecraftDependency,
                "loader_version" to loader.getVersion()
            )

            inputs.properties(modMetadata)
            filesMatching("META-INF/neoforge.mods.toml") { expand(modMetadata) }
        }

        remapJar {
            atAccessWideners.add(awName)
        }
    }
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "bawnorton"
            url = uri("https://maven.bawnorton.com/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "${mod.group}.${mod.id}"
            artifactId = "${mod.id}-$loader"
            version = "${mod.version}+$minecraftVersion"

            from(components["java"])
        }
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    val tag = "$loader-${mod.version}+$minecraftVersion"
    changelog = "[Changelog](https://github.com/Bawnorton/AllTheTrims/blob/stonecutter/CHANGELOG.md)"
    displayName = "${mod.name} ${loader.toString().replaceFirstChar { it.uppercase() }} ${mod.version} for $minecraftVersion"
    type = STABLE
    modLoaders.add(loader.toString())

    github {
        accessToken = providers.gradleProperty("GITHUB_TOKEN")
        repository = "Bawnorton/AllTheTrims"
        commitish = "stonecutter"
        changelog = getRootProject().file("CHANGELOG.md").readLines().joinToString("\n")
        tagName = tag
    }

    modrinth {
        accessToken = providers.gradleProperty("MODRINTH_TOKEN")
        projectId = "pnsUKrap"
        if(mod.minSupportedVersion == mod.maxSupportedVersion) {
            minecraftVersions.add(mod.minSupportedVersion)
        } else {
            minecraftVersionRange {
                start = mod.minSupportedVersion
                end = mod.maxSupportedVersion
            }
        }
        if(loader.isFabric) {
            requires {
                slug = "fabric-api"
            }
        }
    }

    curseforge {
        accessToken = providers.gradleProperty("CURSEFORGE_TOKEN")
        projectId = "876154"
        if(mod.minSupportedVersion == mod.maxSupportedVersion) {
            minecraftVersions.add(mod.minSupportedVersion)
        } else {
            minecraftVersionRange {
                start = mod.minSupportedVersion
                end = mod.maxSupportedVersion
            }
        }
        if(loader.isFabric) {
            requires {
                slug = "fabric-api"
            }
        }
    }
}
