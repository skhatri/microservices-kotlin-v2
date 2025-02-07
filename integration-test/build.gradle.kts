java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val jupiterVersion = project.findProperty("jupiter.version") as String? ?: "5.11.4"
val junitPlatformVersion = project.findProperty("junitplatform.version") as String? ?: "1.11.4"

dependencies {
    testImplementation("com.intuit.karate:karate-junit5:1.4.1")

    testImplementation("io.projectreactor:reactor-test:3.5.2")
    testImplementation("org.mockito:mockito-core:5.15.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:$jupiterVersion")

    testImplementation("org.junit.platform:junit-platform-commons:$junitPlatformVersion")
    testImplementation("org.junit.platform:junit-platform-runner:$junitPlatformVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-engine:$junitPlatformVersion")


}

tasks.test {
    useJUnitPlatform()
}
