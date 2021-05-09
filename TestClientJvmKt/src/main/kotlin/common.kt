package common

import com.google.cloud.firestore.FirestoreOptions
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*


// set GOOGLE_APPLICATION_CREDENTIALS=path/to/firestoreCredetialFile.json
val db = FirestoreOptions.getDefaultInstance().service!!

// https://jp.ktor.work/clients/index.html
val httpClient = HttpClient(CIO) {
    install(JsonFeature) { serializer = KotlinxSerializer() }
    install(HttpTimeout) {
        requestTimeoutMillis = 120 * 1000
        connectTimeoutMillis = 120 * 1000
    }
}