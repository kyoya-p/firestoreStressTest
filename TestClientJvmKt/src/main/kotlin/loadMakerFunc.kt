package loadMakerFunc

import common.httpClient
import common.urlBase
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Serializable
data class Request(val id: String, val nReq: Int, val tCall: String) {
    fun url(nRound: Int) = "$urlBase/loadMakerFuncs?id=$id&fn=$nRound&nr=$nReq&tc=$tCall"
}

@Serializable
data class Result(val id: String, val tc: Long, val te: Long, val cr: Long)

fun now() = Clock.System.now().toEpochMilliseconds()
val org = now()

suspend fun load(nMulti: Int, nMsg: Int) = coroutineScope {
    (0 until nMulti).map { i ->
        async {
            val req = Request("999", nMsg, "main,${now()}")
            println("warmup, ${now() - org}, nMulti, ${req.url(i % 20)}")
            val url = req.url(i % 20)
            httpClient.get<Result>(url)
        }
    }.awaitAll().map { it.also { println(it) } }.sumOf { it.cr }
}

@ExperimentalTime
fun main(args: Array<String>): Unit = runBlocking {
    println("org: $org")
//val (nReq, nRound, nMsg) = args.map { it.toInt() }

    // warmup
    listOf(1, 2, 4, 8, 16).forEach { nMulti ->
        load(nMulti, 200)
        println("delay, ${now() - org}")
        delay(5 * 1000)
    }
    println("total time required: ${now() - org}")
}


