//file:noinspection SpellCheckingInspection
//file:noinspection GroovyAssignabilityCheck
//import com.github.spotbugs.snom.SpotBugsTask
import java.text.SimpleDateFormat
//import org.gradle.crypto.checksum.Checksum
buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    id("org.cadixdev.licenser") version "0.6.1"
//    id "com.github.sherter.google-java-format" version "0.9"
    id("xyz.jpenilla.run-paper") version "2.0.1"
//    id "com.github.spotbugs" version "5.0.13"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
//    id "com.palantir.git-version" version "1.0.0"
    id("com.moonlitdoor.git-version") version "0.1.1"

    id("name.remal.common-ci") version "1.5.0"

//    id "org.gradle.crypto.checksum" version "1.4.0"
    java
    jacoco
    idea
    checkstyle
    signing
    `maven-publish`
    kotlin("jvm") version "1.7.0"
    kotlin("kapt") version "1.7.0"
}

// Thanks BanManager, but your log message is now my property
logger.lifecycle("""
*******************************************
 You are building TownyPlus!
 If you encounter trouble:
 1) Try running "build" in a separate Gradle run
 2) Use gradlew and not gradle
 3) If you still need help, ask on Discord #tickets! https://2v1.me/discord
 Output files will be in /build/libs
*******************************************
""")

apply from: "$rootDir/gradle/jacoco.gradle"
apply from: "$rootDir/gradle/publish.gradle"

if (project.hasProperty("local_script")) {
    apply from: file(local_script + "/build.local.gradle")
}
static def getTime() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd-HHmm")
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
    return sdf.format(new Date()).toString()
}

tasks.withType(JavaCompile) {
    options.encoding = "UTF-8"
    sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

ext {
    mcVersion = project.property("mcVersion") as String
}

group project.property("group") as String
//def details = versionDetails()

//spotbugs {
//    ignoreFailures = false
//    showStackTraces = true
//    showProgress = true
//    effort = "max"
//    reportLevel = "default"
//}


// CI channels will clash if you change the version within build.gradle, this is only for local builds

val version: String? by project
val packageName: String by project

if (System.getenv("CI") == null) {
    if (version == null || version == "") {
        version = getTime() + "-SNAPSHOT"
    } else if (gitBranchName.contains("alpha") || gitBranchName.contains("beta") || gitBranchName.contains("rc")) {
        version = version + "-" + gitBranchName + "-SNAPSHOT";
    } else {
            version = version
    }
}
println("Version: " + version)
// The current version based on the most recent tag on the current git branch.
println("Git Version: " + gitVersion)
println("Branch: " + gitBranchName)
println("Is CI detected? " + System.getenv("CI"))

// checkstyle {
//     // toolVersion "10.6.0"
//     // maxWarnings = 0
// }


tasks.withType(Checkstyle) {
    reports {
        xml.required = true
        html.required = true
    }
}

//tasks.withType(SpotBugsTask) {
//    reports {
//        xml {
//            required.set(true)
//        }
//        html {
//            required.set(true)
//        }
//    }
//}


bukkit {

    // Plugin main class (required)
    main = packageName + ".TownyPlusMain"

    // API version (should be set for 1.13+)
    apiVersion = project.property("apiVersion") as String 

    // Other possible properties from plugin.yml (optional)
    load = "POSTWORLD" // or "POSTWORLD"
    name = project.property("pluginName") as String
    description = project.property("description") as String
    depend = ["Towny"]
    softDepend = ["DiscordSRV", "TownyChat", "VentureChat", "ProtocolLib"]
    defaultPermission = "NOT_OP" // "TRUE", "FALSE", "OP" or "NOT_OP"
    website = "https://github.com/BrycensRanch/TownyPlus"
//    commands {
//        test {
//            description = "This is a test command!"
//            aliases = ["t"]
//            permission = "testplugin.test"
//            usage = "Just run the command!"
//            // permissionMessage = "You may not test this command!"
//        }
//        // ...
//    }

//    permissions {
//        "testplugin.*" {
//            children = ["testplugin.test"] // Defaults permissions to true
//            // You can also specify the values of the permissions
//            childrenMap = ["testplugin.test": false]
//        }
//        "testplugin.test" {
//            description = "Allows you to run the test command"
//            setDefault("OP") // "TRUE", "FALSE", "OP" or "NOT_OP"
//        }
//    }
}

tasks.compileJava {
    options.fork = true
}
tasks.publish {
    dependsOn "clean"
    dependsOn "build"
    tasks.findByName("build").mustRunAfter "clean"

}
assemble {
        dependsOn(shadowJar)
}
repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.spongepowered.org/maven/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://repo.glaremasters.me/repository/towny/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // using spigot-api
    compileOnly("org.spigotmc:spigot-api:${mcVersion}-R0.1-SNAPSHOT") // The Spigot API with no shadowing. Requires the OSS repo.
    // or using paper-api
//    implementation "io.papermc.paper:paper-api:${mcVersion}-R0.1-SNAPSHOT"
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // Command Handling! https://commandframework.cloud
    implementation("cloud.commandframework:cloud-core:1.8.2")
    implementation("cloud.commandframework:cloud-annotations:1.8.2")
    annotationProcessor("cloud.commandframework:cloud-annotations:1.8.2")
    library("org.projectlombok:lombok:1.18.26")
    library("com.fasterxml.jackson.core:jackson-annotations:2.14.2")
    library("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    annotationProcessor("com.fasterxml.jackson.core:jackson-annotations:2.14.2")
    library("com.fasterxml.jackson.dataformat:jackson-dataformat-properties:2.14.2")
    implementation("cloud.commandframework:cloud-paper:1.8.2")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.8.2")
    // Dependency for our way of command handling
    implementation("me.lucko:commodore:2.2")
    // For handling multi platform configuration, simply amazing
    // library "org.spongepowered:configurate-yaml:4.1.2"
    // Spigot/GitHub Releases Update checker. Am I that lazy? Yes.
    implementation("com.jeff_media:SpigotUpdateChecker:3.0.2")
    // Towny
    compileOnly("com.palmergames.bukkit.towny:towny:0.98.6.9")
    compileOnly("com.palmergames.bukkit:TownyChat:0.45")

    // Stealing all your data lmfao
    compileOnly("org.bstats:bstats-bukkit:3.0.1")
    // better http server than spark
    library("io.javalin:javalin-bundle:5.3.2")
    // DiscordSRV, optional dependency
    compileOnly("com.discordsrv:discordsrv:1.24.0")
//    // JDA for DiscordSRV
    implementation("net.dv8tion:JDA:5.0.0-beta.5") {
        exclude module: "opus-java"
    }
    // VentureChat support for chat listening
    compileOnly("mineverse.aust1n46:venturechat:3.5.0")
    // ProtocolLib (required by venturechat)
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
//    compileOnly "com.github.spotbugs:spotbugs-annotations:4.7.3"

    // Adventure
    implementation("net.kyori:adventure-api:4.12.0")
    implementation("net.kyori:adventure-platform-bukkit:4.2.0")
    implementation("net.kyori:adventure-text-minimessage:4.12.0")

    // Test dependencies
//    spotbugsPlugins "com.h3xstream.findsecbugs:findsecbugs-plugin:1.12.0"
//    testCompileOnly "com.github.spotbugs:spotbugs-annotations:4.7.3"
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-core:5.1.1")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.19:2.145.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
}
tasks.shadowJar {
        archivesBaseName.set(project.property("pluginName") as)
        archiveClassifier.set("")
        dependencies {
        // These are sorted by internal dependencies to ones for compatability/functionatlity related to other plugins
        include(dependency("net.kyori::"))
        include(dependency("org.bstats::"))
        include(dependency("com.jeff_media::"))
        include(dependency("me.lucko::"))
        include(dependency("cloud.commandframework::"))
        include(dependency("com.palmergames.bukkit::"))
        include(dependency("com.github.TownyAdvanced::"))
        include(dependency("com.discordsrv::"))
//        include(dependency("github.scarsz.discordsrv::"))
        include(dependency("mineverse.aust1n46::"))
        include(dependency("com.comphenix.protocol::"))
    }
    relocate("cloud.commandframework", "${packageName}.libs.commands")
    relocate("me.lucko", "${packageName}.libs.lucko")
    relocate("com.jeff_media", "${packageName}.libs.jeff_media")
    relocate("org.bstats", "${packageName}.libs.bstats")
    relocate("net.kyori", "${packageName}.libs.kyori")
    relocate("mineverse.aust1n46", "${packageName}.libs.aust1n46")
    relocate("com.comphenix.protocol", "${packageName}.libs.protocol")
    relocate("com.discordsrv", "${packageName}.libs.discordsrv")
    // Exclude signatures, maven/ and proguard/ from META-INF
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    exclude("META-INF/maven/**")
    exclude("META-INF/proguard/**")
}
tasks.license {
    include("**/*.java")
    include("**/*.kt")

    matching("**/*.java") {
        header = file("HEADER.txt")
    }
    matching("**/*.kt") {
        header = file("HEADER.txt")
    }
}

configure<com.gorylenko.GitPropertiesPluginExtension> {
    failOnNoGitDirectory = false
    customProperty "git.mcVersion", { project.property("mcVersion") as String }
}

tasks.sourcesJar {
    enabled = true
}
tasks.build {
    dependsOn(tasks.shadowJar)
}
// tasks.test.dependsOn(shadowJar)

tasks.runServer {
    // Configure the Minecraft version for our task.
    // This is the only required configuration besides applying the plugin.
    // Your plugin"s jar (or shadowJar if present) will be used automatically.
    minecraftVersion(project.property("testServerMCVersion") as String)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events "skipped", "failed"
    }
    // md5: Honestly no one writes plugins with such a focus on testing/quality.
    ignoreFailures = true
}

tasks.processResources {
    project.properties.put("version", version)
    expand(project.properties)
}

defaultTasks "build"
