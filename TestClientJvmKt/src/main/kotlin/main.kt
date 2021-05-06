import com.google.cloud.firestore.FirestoreOptions
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

// set GOOGLE_APPLICATION_CREDENTIALS=path/to/firestoreCredetialFile.json
val firestore = FirestoreOptions.getDefaultInstance().service!!
val httpClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer() // https://jp.ktor.work/clients/http-client/quick-start/requests.html
    } // https://jp.ktor.work/clients/http-client/features/json-feature.html
}

val url = "http://localhost:5001/stress1/us-central1/addMessage" // Local Emu.
//val url = "https://us-central1-stress1.cloudfunctions.net/addMessage" // Cloud


@Serializable
data class Counter(val count: Int, val start: Long, val end: Long)

@ExperimentalTime
suspend fun main(args: Array<String>): Unit = runBlocking {
    val (width, limit) = args.map { it.toInt() }

    // Counter Monitor起動
    //runCounterMonitor(width)

    repeat(100) { devId ->
        launch {
            runClient(devId, width, limit / 5)
        }
    }
}

suspend fun runClient(devId: Int, width: Int, limit: Int) {
    var total = 0
    while (total < limit) {
        val res = httpClient.get<Counter>("$url?width=$width")
        total += res.count

        val wps = res.count * 1000.0 / (res.end - res.start)
        println("$devId, $total,${res.count},${res.end - res.start},$wps")
    }
}

fun runCounterMonitor(width: Int) {
    @Serializable
    data class Counter(val count: Int)

    firestore.collection("counter").limit(width).addSnapshotListener { v, e ->
        v?.documents?.sumBy { it?.data!!["count"] as Int? ?: 0 }?.let {
            print("C: $it \r")
        }
    }
}