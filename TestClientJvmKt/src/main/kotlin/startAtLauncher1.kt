package startAtLauncher1

import common.httpClient
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

//val url = "http://localhost:5001/stress1/us-central1/startAtLauncher1" // Local Emu.
val url = "https://us-central1-stress1.cloudfunctions.net/startAtLauncher1" // Cloud★★★

// .../startAtLauncher/?id=<dev_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>

@Serializable
data class Request(val id: String, val nReq1: Int, val nReq: Int, val nMsg: Int, val timeToStart: Long) {
    fun url() = "$url?id=$id&nr1=$nReq1&nr=$nReq&nm=$nMsg&ts=$timeToStart"
}

@Serializable
data class Result(
    val id: String,
    val nr1: Int, val nr: Int, val nm: Int, val ts: Long, val te: Long,
    val cr: Int, val res: Long
)


@ExperimentalTime
fun main(args: Array<String>): Unit = runBlocking {
    val (nLaunchReq, nLaunch, nReq, nMsg) = args.map { it.toInt() }
    val tOrg = now()
    val tStart = tOrg + 10 * 1000
    val rs: Long = (0 until nLaunchReq).map { id ->
        val req = Request(id = "$id", nReq1 = nLaunch, nReq = nReq, nMsg = nMsg, timeToStart = tStart)
        println("$id ${now() - tOrg} ${req.url()}")
        async { httpClient.get<Result>(req.url()) }
    }.awaitAll().map { it.also { println(it) } }.sumOf { it.res }
    println("total num of results: $rs")
}

fun now() = Clock.System.now().toEpochMilliseconds()
