package startAt

import common.httpClient
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

//val url = "http://localhost:5001/stress1/us-central1/startAt" // Local Emulator
val url = "https://us-central1-stress1.cloudfunctions.net/startAt" // Cloud★★★

@Serializable
data class Request(val id: String, val nMsg: Int, val timeToStart: Long) {
    fun url() = "$url?id=$id&n=$nMsg&ts=$timeToStart"
}

@Serializable
data class Result(val devid: String, val ts: Long, val end: String, val res: List<String>)

@ExperimentalTime
 fun main(args: Array<String>): Unit = runBlocking {
    val (nDev, nMsg) = args.map { it.toInt() }

    val org = now()
    val tStart = org + 30 * 1000
    (0 until nDev).map { id ->
        val req = Request("$id", nMsg,  tStart)
        println("$id ${now() - org} ${req.url()}")
        async { httpClient.get<Result>(req.url()) }
    }.awaitAll().forEach { res ->
        res.res.forEach { e -> println(e) }
    }
}

fun now() = Clock.System.now().toEpochMilliseconds()

