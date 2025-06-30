plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.todo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
}

application {
    mainClass.set("com.todo.TodoAppKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    jvmArgs = listOf(
        "-Xmx1024m", 
        "-Xms256m",
        "-XX:-HeapDumpOnOutOfMemoryError"  // Disable heap dumps
    )
}

kotlin {
    jvmToolchain(17)
} 