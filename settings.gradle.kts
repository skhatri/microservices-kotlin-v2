rootProject.name="microservices-kotlin-v2"

listOf("app", "load-testing", "integration-test").forEach { folder ->
    include(folder)
    project(":${folder}").projectDir = file(folder)
}
