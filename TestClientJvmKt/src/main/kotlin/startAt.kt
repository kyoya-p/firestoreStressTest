package runAgent

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

//val url = "http://localhost:5001/stress1/us-central1/runAgent" // Local Emu.
val url = "https://us-central1-stress1.cloudfunctions.net/runAgent" // Cloud

@Serializable
data class Result(val devId: String, val count:Int, val start: Long, val msg: String)

@ExperimentalTime
suspend fun main(args: Array<String>): Unit = runBlocking {
    val (devNum) = args.map { it.toInt() }

    (0 until devNum).map { id ->
        println(id)
        yield()
        async { httpClient.get<Result>("$url?id=$id&n=5") }
    }.awaitAll().forEach {
        println("$it")
    }
}

fun now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

