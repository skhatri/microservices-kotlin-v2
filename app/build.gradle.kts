import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.jacoco)
    alias(libs.plugins.kotlin.spring)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JavaVersion.VERSION_21.toString()
    }
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
    all {
        exclude(module = "spring-boot-starter-logging")
        exclude(module = "logback-core")
        exclude(module = "logback-classic")
    }
}



dependencies {
    implementation(libs.bundles.spring.boot)
    implementation(libs.bundles.kotlin.app)
    implementation(libs.bundles.app)
    implementation(libs.bundles.pg)

    if (project.ext["server.type"] == "reactor-netty") {
        implementation(libs.netty.tcnative)
    }
    testImplementation(libs.bundles.test.core)
    testRuntimeOnly(libs.bundles.test.runtime)
}

tasks.test {
    useJUnitPlatform()
}

sonarqube {
    properties {
        property("sonar.projectName", "microservices-starter-kotlin")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.projectKey", "microservices-starter-kotlin-app")
        property("sonar.projectVersion", "${project.version}")
        property("sonar.junit.reportPaths", "${projectDir}/build/test-results/test")
        property("sonar.coverage.jacoco.xmlReportPaths", "${projectDir}/build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.coverage.exclusions", "**/R.kt")
        property("sonar.language", "kotlin")
    }
}


tasks.jacocoTestReport {
    reports {
        xml.required = false
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            enabled = true
            limit {
                minimum = "0.2".toBigDecimal()
            }
        }

        rule {
            enabled = false
            element = "BUNDLE"
            includes = listOf("com.github.starter.*")
            excludes = listOf("**/Application*")
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.1".toBigDecimal()
            }
        }
    }
}

tasks.test {
    extensions.configure(JacocoTaskExtension::class) {
        destinationFile = file("${projectDir}/build/jacoco/jacocoTest.exec")
        classDumpDir = file("${projectDir}/build/jacoco/classpathdumps")
    }
}

tasks.test {
    finalizedBy("jacocoTestReport")
}

tasks.check {
    dependsOn(arrayOf("jacocoTestReport", "jacocoTestCoverageVerification"))
}

task("runApp", JavaExec::class) {
    mainClass = "com.github.starter.ApplicationKt"
    classpath = sourceSets["main"].runtimeClasspath
    jvmArgs = listOf(
        "-Xms512m", "-Xmx512m"
    )
}

kotlin {
    jvmToolchain(21)
}
