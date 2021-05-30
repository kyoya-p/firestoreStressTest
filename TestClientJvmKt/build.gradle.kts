plugins {
    kotlin("jvm") version "1.5.10" // https://kotlinlang.org/docs/gradle.html
    kotlin("plugin.serialization") version "1.5.10" // https://github.com/Kotlin/kotlinx.serialization
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")// https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.0") // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-core
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1") // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-datetime
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.0") // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-core

    implementation("com.google.cloud:google-cloud-firestore:2.3.0") // https://mvnrepository.com/artifact/com.google.cloud/google-cloud-firestore
    //implementation("com.google.firebase:firebase-auth:20.0.4") //https://mvnrepository.com/artifact/com.google.firebase/firebase-auth
    implementation("io.ktor:ktor-client-core:1.5.4") // https://mvnrepository.com/artifact/io.ktor/ktor-client-core
    implementation("io.ktor:ktor-client-json:1.5.4") // https://mvnrepository.com/artifact/io.ktor/ktor-client-json
    implementation("io.ktor:ktor-client-serialization:1.5.4") // https://mvnrepository.com/artifact/io.ktor/ktor-client-serialization
    implementation("io.ktor:ktor-client-cio:1.5.4") // https://mvnrepository.com/artifact/io.ktor/ktor-client-core

    implementation("joda-time:joda-time:2.10.10") // https://mvnrepository.com/artifact/io.ktor/ktor-client-core
}
