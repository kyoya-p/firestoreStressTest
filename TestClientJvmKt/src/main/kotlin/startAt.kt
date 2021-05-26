package startAt

import common.httpClient
import common.urlBase
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class Request(val id: String, val nMsg: Int, val timeToStart: Long) {
    fun url(rr: Int) = "$urlBase/startAt$rr?id=$id&n=$nMsg&ts=$timeToStart"
}

@Serializable
data class Result(val id: String, val tc: Long, val ts: Long, val te: Long, val cr: Long, val cs: Long)


val round = 20

@ExperimentalTime
fun main(args: Array<String>): Unit = runBlocking {
    val (nDev, nMsg) = args.map { it.toInt() }

    val org = now()
    val tStart = org + 5 * 1000
    val rs = (0 until nDev).map { id ->
        //delay(2)
        val req = Request("$id-${id % round}", nMsg, tStart)
        println("$id ${now() - org} ${req.url(id % round)}")
        async { httpClient.get<Result>(req.url(id % round)) }
    }.awaitAll().map { it.also { println(it) } }.sumOf { it.cs }
    println("total num of results: $rs")
}

fun now() = Clock.System.now().toEpochMilliseconds()

