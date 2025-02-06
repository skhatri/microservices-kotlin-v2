rootProject.name="microservices-kotlin-v2"

listOf("app", "load-testing").forEach { folder ->
    include(folder)
    project(":${folder}").projectDir = file(folder)
}
