package startAt2

import common.httpClient
import common.urlBase
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class Request(val id: String, val nMsg: Int, val timeToStart: Long) {
    fun url(rr: Int) = "$urlBase/startAt$rr?id=$id&n=$nMsg&ts=$timeToStart"
}

@Serializable
data class Result(val id: String, val tc: Long, val ts: Long, val te: Long, val cr: Long, val cs: Long)


val round = 1

@ExperimentalTime
fun main(args: Array<String>): Unit = runBlocking {
    val (nDev, nMsg) = args.map { it.toInt() }
    val org = now()
    val tStart = org + 0 * 1000

    val sem = Semaphore(50)
    val rs = (0 until nDev).map { id ->
        async {
            sem.withPermit {
                val req = Request("${id}R${id % round}", nMsg, tStart)
                println("$id ${now() - org}, ${sem.availablePermits}, ${req.url(id % round)}")
                val url = req.url(id % round)
                httpClient.get<Result>(url)
            }
        }
    }.awaitAll().map { it.also { println(it) } }.sumOf { it.cs }
    println("total num of results: $rs")
    println("total time required: ${now() - tStart}")
}

fun now() = Clock.System.now().toEpochMilliseconds()

