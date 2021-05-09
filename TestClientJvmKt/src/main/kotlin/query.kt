package query

import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.annotation.ServerTimestamp
import common.db
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
suspend fun main(args: Array<String>): Unit = runBlocking {
    val (nMsg) = args.map { it.toInt() }

    data class TimeResult(
        val id: String, val time: Long, val now: String,
        val tSvr: FieldValue
    )

    db.collection("test").document("doc")
        .set(TimeResult(id = "", time = 1, now = "a", tSvr = FieldValue.serverTimestamp())).get()
    val r = db.collection("test").document("doc").get().get()
    println(r.data)
    /*
    db.collection("messages").orderBy("time", Query.Direction.DESCENDING).limit(nMsg)
        .get().get().documents.mapNotNull { it to it.getTimestamp() }.forEach {
            println("${it["id"]}, ${it["time"]}")
        }
 */
}

fun now() = Clock.System.now().toEpochMilliseconds()

