@file:Suppress("UnstableApiUsage")

plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.22"
    id("dev.architectury.loom") version "1.6-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
}

class ModData {
    val id = property("mod_id").toString()
    val name = property("mod_name").toString()
    val version = property("mod_version").toString()
    val group = property("mod_group").toString()
    val minecraftDependency = property("minecraft_dependency").toString()
    val minSupportedVersion = property("mod_min_supported_version").toString()
    val maxSupportedVersion = property("mod_max_supported_version").toString()
}

class LoaderData {
    private val name = loom.platform.get().name.lowercase()
    val isFabric = name == "fabric"
    val isForge = name == "forge"
    val isNeoForge = name == "neoforge"

    fun getVersion() : String {
        return if(isForge) {
            property("loader_forge").toString()
        } else if (isNeoForge) {
            property("loader_neoforge").toString()
        } else {
            property("fabric_loader").toString()
        }
    }

    override fun toString(): String {
        return name
    }
}

class MinecraftVersionData {
    private val name = stonecutter.current.version.substringBeforeLast("-")

    fun equalTo(other: String) : Boolean {
        return stonecutter.compare(name, other.lowercase()) == 0
    }

    fun greaterThan(other: String) : Boolean {
        return stonecutter.compare(name, other.lowercase()) > 0
    }

    fun lessThan(other: String) : Boolean {
        return stonecutter.compare(name, other.lowercase()) < 0
    }

    override fun toString(): String {
        return name
    }
}

fun DependencyHandler.neoForge(dep: Any) = add("neoForge", dep)
fun DependencyHandler.forge(dep: Any) = add("forge", dep)
fun DependencyHandler.forgeRuntimeLibrary(dep: Any) = add("forgeRuntimeLibrary", dep)

val mod = ModData()
val loader = LoaderData()
val minecraftVersion = MinecraftVersionData()
val awName = "allthetrims.accesswidener"

version = "${mod.version}+$minecraftVersion"
group = mod.group
base.archivesName.set(mod.name)

repositories {
    mavenCentral()
    maven("https://cursemaven.com")
    maven("https://api.modrinth.com/maven")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
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

tasks.withType<JavaCompile> {
    options.release = 21
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }
}

if(loader.isFabric) {
    dependencies {
        modImplementation("net.fabricmc:fabric-loader:${loader.getVersion()}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api")}")

        modImplementation("com.terraformersmc:modmenu:${property("mod_menu")}")
        modImplementation("dev.isxander:yet-another-config-lib:${property("yacl")}+$minecraftVersion-$loader")

        modCompileOnly("maven.modrinth:iris:${property("iris")}")
        modImplementation("maven.modrinth:elytra-trims:${property("elytra_trims")}")

        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
    }

    tasks.processResources {
        val map = mapOf(
            "version" to mod.version,
            "minecraft_dependency" to mod.minecraftDependency
        )

        inputs.properties(map)
        filesMatching("fabric.mod.json") { expand(map) }
    }
}

if (loader.isForge) {
    dependencies {
        forge("net.minecraftforge:forge:$minecraftVersion-${loader.getVersion()}")

        compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:${property("mixin_extras")}")!!)
        implementation(include("io.github.llamalad7:mixinextras-forge:${property("mixin_extras")}")!!)

        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
    }

    loom {
        forge {
            convertAccessWideners = true
            mixinConfig("${mod.id}.mixins.json")
            mixinConfig("${mod.id}-client.mixins.json")
        }
    }

    tasks.processResources {
        val map = mapOf(
            "version" to mod.version,
            "minecraft_dependency" to mod.minecraftDependency,
            "loader_version" to loader.getVersion()
        )

        inputs.properties(map)
        filesMatching("META-INF/mods.toml") { expand(map) }
    }

    sourceSets.forEach {
        val dir = layout.buildDirectory.dir("sourceSets/${it.name}").get().asFile
        it.output.setResourcesDir(dir)
        it.java.destinationDirectory = dir
    }
}

if (loader.isNeoForge) {
    dependencies {
        neoForge("net.neoforged:neoforge:${loader.getVersion()}")

        modImplementation("dev.isxander:yet-another-config-lib:${property("yacl")}+$minecraftVersion-$loader")
        forgeRuntimeLibrary(runtimeOnly("org.quiltmc.parsers:json:${findProperty("quilt_parsers")}")!!)
        forgeRuntimeLibrary(runtimeOnly("org.quiltmc.parsers:gson:${findProperty("quilt_parsers")}")!!)

        mappings(loom.layered {
            mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
            if(loader.isNeoForge) {
                if (minecraftVersion.lessThan("1.21")) {
                    mappings("dev.architectury:yarn-mappings-patch-neoforge:1.20.5+build.3")
                } else {
                    mappings(file("mappings/fix.tiny"))
                }
            }
        })
    }

    tasks {
        processResources {
            val map = mapOf(
                "version" to mod.version,
                "minecraft_dependency" to mod.minecraftDependency,
                "loader_version" to loader.getVersion()
            )

            inputs.properties(map)
            filesMatching("META-INF/neoforge.mods.toml") { expand(map) }
        }

        remapJar {
            atAccessWideners.add(awName)
        }
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    val tag = "$loader-${mod.version}+$minecraftVersion"
    changelog = "[Changelog](https://github.com/Bawnorton/Neruina/blob/stonecutter/CHANGELOG.md)"
    displayName = "${mod.name} ${loader.toString().replaceFirstChar { it.uppercase() }} ${mod.version} for $minecraftVersion"
    type = STABLE
    modLoaders.add(loader.toString())

    dryRun = false

    github {
        accessToken = providers.gradleProperty("GITHUB_TOKEN")
        repository = "Bawnorton/Neruina"
        commitish = "stonecutter"
        changelog = getRootProject().file("CHANGELOG.md").readLines().joinToString("\n")
        tagName = tag
    }

    modrinth {
        accessToken = providers.gradleProperty("MODRINTH_TOKEN")
        projectId = "1s5x833P"
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
        projectId = "851046"
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
