package addMessage

import com.google.cloud.firestore.FirestoreOptions
import common.httpClient
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.threeten.bp.DateTimeUtils.toLocalDateTime
import kotlin.time.ExperimentalTime

val url = "http://localhost:5001/stress1/us-central1/addMessage" // Local Emu.
//val url = "https://us-central1-stress1.cloudfunctions.net/addMessage" // Cloud


@Serializable
data class Counter(val count: Int, val start: Long, val end: Long)

@ExperimentalTime
suspend fun main(args: Array<String>): Unit = runBlocking {
    val (devNum, limit) = args.map { it.toInt() }

    repeat(devNum) { devId ->
        launch {
            runClient(devId, limit )
        }
    }
}

fun now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

suspend fun runClient(devId: Int, loop: Int) = runCatching {
    println("start: ${now()} $devId")
    var total = 0
//    while (total < limit) {
    repeat(loop) {
        val res = httpClient.get<Counter>("$url?width=1")
        total += res.count

        val wps = res.count * 1000.0 / (res.end - res.start)
        println("$devId, $total,${res.count},${res.end - res.start},$wps")
    }
}.onFailure { it.printStackTrace() }.getOrThrow()

