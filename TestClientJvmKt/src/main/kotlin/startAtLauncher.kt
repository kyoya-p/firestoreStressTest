package startAtLauncher

import common.httpClient
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

//val url = "http://localhost:5001/stress1/us-central1/startAtLauncher" // Local Emu.
val url = "https://us-central1-stress1.cloudfunctions.net/startAtLauncher" // Cloud★★★

// .../startAtLauncher/?id=<dev_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>

@Serializable
data class Request(val id: String, val nReq: Int, val nMsg: Int, val timeKeepUntil: Long) {
    fun url() = "$url?id=$id&nr=$nReq&nm=$nMsg&ts=$timeKeepUntil"
}

@Serializable
data class Result(val res: List<String>)

@ExperimentalTime
suspend fun main(args: Array<String>): Unit = runBlocking {
    val (nLaunchReq, nLaunch, nMsg) = args.map { it.toInt() }
    val tOrg = now()
    val tStart = tOrg + 30 * 1000
    (0 until nLaunchReq).map { id ->
        fun now() = Clock.System.now().toEpochMilliseconds()
        val req = Request("$id", nLaunch, nMsg, tStart)
        println("$id ${now() - tOrg} ${req.url()}")
        async { httpClient.get<Result>(req.url()) }
    }.awaitAll().forEach {  println(it) }

}

fun now() = Clock.System.now().toEpochMilliseconds()

