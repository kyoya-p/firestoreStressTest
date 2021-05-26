package startAtLauncher

import com.google.api.client.json.Json
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import common.httpClient
import common.urlBase
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

//val url = "http://localhost:5001/stress1/us-central1/startAtLauncher" // Local Emu.
val url = "$urlBase/startAtLauncher" // Cloud★★★

// .../startAtLauncher/?id=<dev_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>

@Serializable
data class Request(val id: String, val nReq: Int, val nMsg: Int, val timeKeepUntil: Long) {
    fun url() = "$url?id=$id&nr=$nReq&nm=$nMsg&ts=$timeKeepUntil"
}

@Serializable
data class Result(val id: String, val tc: Long, val ts: Long, val te: Long, val cr: Long, val cs: Long)


@ExperimentalTime
fun main(args: Array<String>): Unit = runBlocking {
    val (nLaunchReq, nLaunch, nMsg) = args.map { it.toInt() }
    val tOrg = now()
    val tStart = tOrg + 5 * 1000
    val rs = (0 until nLaunchReq).map { id ->
        val req = Request("$id", nLaunch, nMsg, tStart)
        println("$id ${now() - tOrg} ${req.url()}")
        async { httpClient.get<Result>(req.url()) }
    }.awaitAll().map { it.also { println(it) } }.sumOf { it.cs }
    println("total num of results: $rs")
}

fun now() = Clock.System.now().toEpochMilliseconds()

