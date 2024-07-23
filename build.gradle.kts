@file:Suppress("UnstableApiUsage")

plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.22"
    id("dev.architectury.loom") version "1.7-SNAPSHOT"
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

class CompatMixins {
    private var common : List<String> = listOf(
        "rei.DefaultClientPluginMixin",
        "emi.VanillaPluginMixin",
        "jei.SmithingRecipeCategoryMixin"
    )

    private var fabric : List<String> = listOf(
        "fabric.mythicmetals.MythicMetalsClientMixin",
        "fabric.wildfiregender.GenderArmorLayerMixin",
        "fabric.bclib.CustomModelBakeryMixin"
    )

    private var neoforge : List<String> = listOf()

    fun getMixins() : Map<String, String> {
        val mixins = common + if(loader.isFabric) fabric else neoforge
        return mapOf(
            "compat_mixins" to "[\n${mixins.joinToString(",\n") { "\"$it\"" }}\n]"
        )
    }
}

fun DependencyHandler.neoForge(dep: Any) = add("neoForge", dep)
fun DependencyHandler.forge(dep: Any) = add("forge", dep)
fun DependencyHandler.forgeRuntimeLibrary(dep: Any) = add("forgeRuntimeLibrary", dep)

val mod = ModData()
val loader = LoaderData()
val minecraftVersion = MinecraftVersionData()
val awName = "allthetrims.accesswidener"

version = "${mod.version}-$loader+$minecraftVersion"
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
    maven("https://jitpack.io")
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

    processResources {
        val compatMixins = CompatMixins().getMixins()

        inputs.properties(compatMixins)
        filesMatching("allthetrims-compat.mixins.json") { expand(compatMixins) }
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
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api")}+$minecraftVersion")

        modImplementation("com.terraformersmc:modmenu:${property("mod_menu")}")

        modCompileOnly("maven.modrinth:elytra-trims:${property("elytra_trims")}")
        modCompileOnly("maven.modrinth:iris:${property("iris")}+$minecraftVersion")
        modCompileOnly("maven.modrinth:show-me-your-skin:${property("show_me_your_skin")}+$minecraftVersion")
        modCompileOnly("maven.modrinth:female-gender:${property("wildfire_gender")}+$minecraftVersion")
        modCompileOnly("maven.modrinth:mythicmetals:${property("mythic_metals")}")

        modCompileOnly("maven.modrinth:bclib:${property("bclib")}")
        modCompileOnly("maven.modrinth:betterend:${property("better_end")}")

        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
    }

    tasks {
        processResources {
            val modMetadata = mapOf(
                "version" to mod.version,
                "minecraft_dependency" to mod.minecraftDependency
            )

            inputs.properties(modMetadata)
            filesMatching("fabric.mod.json") { expand(modMetadata) }
        }
    }
}

if (loader.isNeoForge) {
    dependencies {
        neoForge("net.neoforged:neoforge:${loader.getVersion()}")

        modCompileOnly(fileTree("libs") {
            include("*.jar")
        })

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

    dryRun = false

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
