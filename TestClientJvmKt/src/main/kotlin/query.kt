package query

import com.google.cloud.Timestamp
import com.google.cloud.firestore.Query
import common.db
import kotlinx.coroutines.*
import kotlin.time.ExperimentalTime

fun Timestamp.toMillisec() = seconds * 1000 + nanos / 1000 / 1000

@ExperimentalTime
fun main(args: Array<String>): Unit = runBlocking {
    val (nMsg) = args.map { it.toInt() }
    val res = db.collection("messages")
        //.orderBy("svrtime", Query.Direction.DESCENDING)
        .limit(nMsg)
        .get().get().documents.mapNotNull { it.data }
    val t0 = (res[0]["svrtime"] as Timestamp).toMillisec()
    res.forEachIndexed { i, e ->
        val ts = e["svrtime"] as Timestamp
        val t = ts.seconds * 1000 + ts.nanos / 1000 / 1000
        println("$i, ${e["id"]}, ${e["time"]}, ${t0}, ${t0 - t} ")
    }
}


