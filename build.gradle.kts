tasks.wrapper {
    gradleVersion = "8.14.2"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.21")
    }
}

allprojects {
    apply(plugin = "idea")
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

}

