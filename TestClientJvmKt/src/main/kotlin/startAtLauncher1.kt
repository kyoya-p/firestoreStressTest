package startAtLauncher1

import common.httpClient
import common.urlBase
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.time.ExperimentalTime

//val url = "http://localhost:5001/stress1/us-central1/startAtLauncher1" // Local Emu.
val url = "$urlBase/startAtLauncher1" // Cloud★★★

// .../startAtLauncher/?id=<dev_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>

@Serializable
data class Request(val id: String, val nReq1: Int, val nReq: Int, val nMsg: Int, val timeToStart: Long) {
    fun url() = "$url?id=$id&nr1=$nReq1&nr=$nReq&nm=$nMsg&ts=$timeToStart"
}

@Serializable
data class Result(val id: String, val tc: Long, val ts: Long, val te: Long, val cr: Long, val cs: Long) {
    fun println(): Result {
        println("$id, \"${Date(tc)}\", ${ts - tc}, ${te - tc} $this")
        return this
    }
}

@ExperimentalTime
fun main(args: Array<String>): Unit = runBlocking {
    val (nLaunchReq, nLaunch, nReq, nMsg) = args.map { it.toInt() }
    val tOrg = now()
    val tStart = tOrg + 15 * 1000
    val rs: Long = (0 until nLaunchReq).map { id ->
        val req = Request(id = "$id", nReq1 = nLaunch, nReq = nReq, nMsg = nMsg, timeToStart = tStart)
        println("$id ${now() - tOrg} ${req.url()}")
        async { httpClient.get<Result>(req.url()) }
    }.awaitAll().map { it.println() }.sumOf { it.cs }
    println("total num of results: $rs")
}

fun now() = Clock.System.now().toEpochMilliseconds()

