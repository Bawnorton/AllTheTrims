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
    val isNeoForge = name == "neoforge"

    fun getVersion() : String = if (isNeoForge) {
        property("loader_neoforge").toString()
    } else {
        property("fabric_loader").toString()
    }

    override fun toString(): String {
        return name
    }
}

class MinecraftVersionData {
    private val name = stonecutter.current.version.substringBeforeLast("-")

    fun equalTo(other: String) : Boolean = stonecutter.compare(name, other.lowercase()) == 0
    fun greaterThan(other: String) : Boolean = stonecutter.compare(name, other.lowercase()) > 0
    fun lessThan(other: String) : Boolean = stonecutter.compare(name, other.lowercase()) < 0

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
    maven("https://maven.ladysnake.org/releases")
    maven("https://maven.enjarai.dev/releases")
    maven("https://maven.shedaniel.me")
    maven("https://maven.blamejared.com/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    modImplementation("dev.isxander:yet-another-config-lib:${property("yacl")}+$minecraftVersion-$loader")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-$loader:${property("rei")}")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-$loader:${property("rei")}")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-$loader:${property("rei")}")
    modCompileOnly("me.shedaniel.cloth:cloth-config-$loader:${property("cloth_config")}")

    modCompileOnly("dev.emi:emi-xplat-intermediary:${property("emi")}+$minecraftVersion:api")
    modCompileOnly("dev.emi:emi-xplat-intermediary:${property("emi")}+$minecraftVersion")

    modCompileOnly("mezz.jei:jei-$minecraftVersion-$loader-api:${property("jei")}") { isTransitive = false }
    modImplementation("mezz.jei:jei-$minecraftVersion-$loader:${property("jei")}") { isTransitive = false }

    modCompileOnly(fileTree("libs") {
        include("**/*.jar") // deps on mods that haven't been released
    })
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
        options.release = 21
    }
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

        modCompileOnly("maven.modrinth:iris:${property("iris")}")
        modImplementation("maven.modrinth:elytra-trims:${property("elytra_trims")}")
        modImplementation("maven.modrinth:show-me-your-skin:${property("show_me_your_skin")}")

        modRuntimeOnly("nl.enjarai:cicada-lib:${property("cicada_lib")}")
        modRuntimeOnly("org.ladysnake.cardinal-components-api:cardinal-components-base:${property("cardinal_components")}")
        modRuntimeOnly("org.ladysnake.cardinal-components-api:cardinal-components-entity:${property("cardinal_components")}")

        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
    }

    tasks {
        processResources {
            val modMetadata = mapOf(
                "version" to mod.version,
                "minecraft_dependency" to mod.minecraftDependency
            )

            val compatMixins = mapOf(
                "compat_mixins" to """""",
                "compat_client_mixins" to """
                "elytratrims.TrimOverlayRendererMixin",
                "wildfiregender.fabric.GenderArmorLayerMixin",
                "rei.DefaultClientPluginMixin",
                "emi.VanillaPluginMixin",
                "jei.SmithingRecipeCategoryMixin"
            """
            )

            inputs.properties(modMetadata)
            inputs.properties(compatMixins)
            filesMatching("fabric.mod.json") { expand(modMetadata) }
            filesMatching("allthetrims-compat.mixins.json") { expand(compatMixins) }
        }
    }
}

if (loader.isNeoForge) {
    dependencies {
        neoForge("net.neoforged:neoforge:${loader.getVersion()}")

        forgeRuntimeLibrary(runtimeOnly("org.quiltmc.parsers:json:${findProperty("quilt_parsers")}")!!)
        forgeRuntimeLibrary(runtimeOnly("org.quiltmc.parsers:gson:${findProperty("quilt_parsers")}")!!)

        mappings(loom.layered {
            mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
            if(loader.isNeoForge) {
                mappings(file("mappings/fix.tiny"))
            }
        })
    }

    tasks {
        processResources {
            val modMetadata = mapOf(
                "version" to mod.version,
                "minecraft_dependency" to mod.minecraftDependency,
                "loader_version" to loader.getVersion()
            )

            val compatMixins = mapOf(
                "compat_mixins" to """""",
                "compat_client_mixins" to """
                    "rei.DefaultClientPluginMixin",
                    "emi.VanillaPluginMixin",
                    "jei.SmithingRecipeCategoryMixin"
                """
            )

            inputs.properties(modMetadata)
            inputs.properties(compatMixins)
            filesMatching("META-INF/neoforge.mods.toml") { expand(modMetadata) }
            filesMatching("allthetrims-compat.mixins.json") { expand(compatMixins) }
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
