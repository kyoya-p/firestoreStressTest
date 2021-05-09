package httpClient

import com.google.cloud.firestore.FirestoreOptions
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.threeten.bp.DateTimeUtils.toLocalDateTime
import kotlin.time.ExperimentalTime

// set GOOGLE_APPLICATION_CREDENTIALS=path/to/firestoreCredetialFile.json
val firestore = FirestoreOptions.getDefaultInstance().service!!

// https://jp.ktor.work/clients/index.html
val httpClient = HttpClient(CIO) {
    install(JsonFeature) { serializer = KotlinxSerializer() }
    install(HttpTimeout) {
        requestTimeoutMillis = 60 * 1000
        connectTimeoutMillis = 60 * 1000
    }
}