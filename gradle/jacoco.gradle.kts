jacoco { toolVersion = "0.8.8" }

tasks.jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.check.dependsOn "jacocoTestReport"