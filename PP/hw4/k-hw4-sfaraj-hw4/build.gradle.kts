plugins {
    kotlin("jvm") version "2.2.20"
}

group = "org.sigcse.webanddata.whova"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.14.6")
}

tasks.test {
    jvmArgs = listOf("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
    useJUnitPlatform()
}