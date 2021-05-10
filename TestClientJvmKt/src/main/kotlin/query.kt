package query

import com.google.cloud.firestore.Query
import common.db
import common.dbEmu
import kotlinx.coroutines.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
 fun main(args: Array<String>): Unit = runBlocking {
    val (nMsg) = args.map { it.toInt() }
    dbEmu.collection("messages").orderBy("time", Query.Direction.DESCENDING).limit(nMsg)
        .get().get().documents.mapNotNull { it.data }.forEach {
            println("${it["id"]}, ${it["svrtime"]}")
        }
}


