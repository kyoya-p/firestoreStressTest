package startAt

import common.httpClient
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

val url = "http://localhost:5001/stress1/us-central1/startAt" // Local Emulator
//val url = "https://us-central1-stress1.cloudfunctions.net/startAt" // Cloud★★★

@Serializable
data class Request(val devid: String, val n: Int, val et: Long, val ts: Long) {
    fun url() = "$url?id=$devid&n=$n&et=$et&ts=$ts"
}

@Serializable
data class Result(val devid: String, val ts: Long, val end: String, val res: List<String>)

@ExperimentalTime
suspend fun main(args: Array<String>): Unit = runBlocking {
    val (nDev, nMsg) = args.map { it.toInt() }

    val org = now()
    val st = org + 5 * 1000
    (0 until nDev).map { id ->
        val req = Request("$id", nMsg, now(), st)
        println("$id ${now() - org} ${req.url()}")
        async { httpClient.get<Result>(req.url()) }
    }.awaitAll().forEach { res ->
        res.res.forEach { e -> println(e) }
    }
}

fun now() = Clock.System.now().toEpochMilliseconds()

