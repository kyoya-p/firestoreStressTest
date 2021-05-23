package common

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.cloud.ServiceOptions
import com.google.cloud.firestore.FirestoreOptions
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*


// set GOOGLE_APPLICATION_CREDENTIALS=path/to/firestoreCredetialFile.json

val db = FirestoreOptions.getDefaultInstance().service!!

// for emulator
// https://github.com/googleapis/java-firestore/issues/361
val dbEmu0 = FirestoreOptions.getDefaultInstance().toBuilder()
    .setProjectId(ServiceOptions.getDefaultProjectId())
    .setHost("localhost:8080")
    .setCredentials(FirestoreOptions.EmulatorCredentials())
    .setCredentialsProvider(FixedCredentialsProvider.create(FirestoreOptions.EmulatorCredentials()))
    .build()
    .service!!

val dbEmu = FirestoreOptions.getDefaultInstance().toBuilder()
    .setProjectId(ServiceOptions.getDefaultProjectId())
    .setHost("localhost:8080")
    .setCredentials(FirestoreOptions.EmulatorCredentials())
    .build()
    .service!!


// https://jp.ktor.work/clients/index.html
val httpClient = HttpClient(CIO) {
    install(JsonFeature) { serializer = KotlinxSerializer() }
    install(HttpTimeout) {
        requestTimeoutMillis = 300 * 1000
        connectTimeoutMillis = 300 * 1000
    }
}

val region = "asia-northeast2"
val urlBase = "https://$region-stress1.cloudfunctions.net"