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