package query

import com.google.cloud.Timestamp
import com.google.cloud.firestore.Query
import common.db
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Timestamp.toDurationSinceOrigin() = Duration.seconds(seconds) + Duration.nanoseconds(nanos)

@ExperimentalTime
fun Timestamp.toInstant() = Instant.fromEpochMilliseconds(toDurationSinceOrigin().inWholeMilliseconds)

@ExperimentalTime
fun main(args: Array<String>): Unit = runBlocking {
    val (nMsg) = args.map { it.toInt() }
    val res = db.collection("messages")
        //.orderBy("svrtime", Query.Direction.DESCENDING)
        .limit(nMsg)
        .get().get().documents.mapNotNull { it.data }.sortedBy { it["svrtime"] as Timestamp }
    val t0 = (res.last()["svrtime"] as Timestamp).toDurationSinceOrigin()
    res.forEachIndexed { i, e ->
        val ts = e["svrtime"] as Timestamp
        val dt = t0 - ts.toDurationSinceOrigin()
        println(
            "${res.size - i - 1}, ${
                ts.toInstant().toLocalDateTime(TimeZone.currentSystemDefault())
            }, ${e["id"]}, ${e["func"]}, ${e["time"]}, ${dt}${if (dt >= Duration.minutes(1)) "***" else ""} "
        )
    }
}


