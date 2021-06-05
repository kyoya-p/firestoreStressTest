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
            val url = Request("999", nMsg, "main,${now()}").url(i % 20)
            println("warmup, ${now() - org}, nMulti, $url")
            httpClient.get<Result>(url)
        }
    }.awaitAll().map { it.also { println(it) } }.sumOf { it.cr }
}

@ExperimentalTime
fun main(args: Array<String>): Unit = runBlocking {
    println("org: $org")
//val (nReq, nRound, nMsg) = args.map { it.toInt() }

    // warmup
    listOf(1, 1, 1, 1, 3, 3, 3, 6, 6, 9, 12, 15).forEach { nMulti ->
        load(nMulti, 400)
    }
    println("total time required: ${now() - org}")
}


