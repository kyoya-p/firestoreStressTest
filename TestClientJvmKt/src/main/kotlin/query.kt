package query

import com.google.cloud.Timestamp
import com.google.cloud.firestore.Query
import common.db
import common.dbEmu
import kotlinx.coroutines.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main(args: Array<String>): Unit = runBlocking {
    val (nMsg) = args.map { it.toInt() }
    db.collection("messages").orderBy("time", Query.Direction.DESCENDING).limit(nMsg)
        .get().get().documents.mapNotNull { it.data }.forEachIndexed { i, e ->
            val ts = e["svrtime"] as Timestamp
            val t = ts.seconds * 1000 + ts.nanos / 1000 / 1000
            println("$i, ${e["id"]}, ${e["time"]}, ${t}")
        }
}


