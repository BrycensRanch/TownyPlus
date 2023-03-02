java {
    tasks.shadowJar
    withJavadocJar()
    withSourcesJar()
}

signing {
    def signingKeyId = System.getenv("signingKeyId")
    def signingKey = System.getenv("signingKey")
    def signingPassword = System.getenv("signingPassword")
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    useGpgCmd()
    sign publishing.publications
}

tasks.jar {
    manifest {
        attributes (
                "Build-Jdk": "${System.properties["java.vendor"]} ${System.properties["java.vm.version"]}",
                "Built-By":System.properties["user.name"],
                "Built-On":"${System.properties["os.arch"]} ${System.properties["os.name"]} ${System.properties["os.version"]}",
                "Build-Date": new java.text.SimpleDateFormat("yyyy-MM-dd"T"HH:mm:ss.SSSZ").format(new Date()),
                "Created-By": "Gradle ${gradle.gradleVersion}",
                "GHA-JOB": "${System.getenv("GITHUB_JOB")}",
                "GHA-RUNNER": "${System.getenv("RUNNER_NAME")}",
                "GHA-RUN": "${System.getenv("GITHUB_RUN_NUMBER")}",
        )
    }
}

publishing {
    publications {
        shadow(MavenPublication) { publication ->
            artifactId = project.getName().toLowerCase()
            groupId = ((String)project.getGroup()).toLowerCase()
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${project.findProperty("GITHUB_REPOSITORY")?: System.getenv("GITHUB_REPOSITORY")}")
            credentials {
                username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

javadoc {
    if(JavaVersion.current().isJava9Compatible()) {
        options.addBooleanOption("html5", true)
        options.addStringOption("Xdoclint:none", "-quiet")
    }
}
