import java.util.*

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
}

project.extra.set("packageName", name.replace("-", ""))
project.extra.set("pluginName", name.split('-').joinToString("") { it ->
    it.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.getDefault()
    ) else it.toString()
} })

tasks {
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
            expand(project.extra.properties)
        }
    }

    shadowJar {
        from(sourceSets["main"].output)
        archiveBaseName.set(project.extra.properties["pluginName"].toString())
        archiveVersion.set("") // For bukkit plugin update

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }
}
