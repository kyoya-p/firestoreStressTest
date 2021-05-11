package startAtLauncher

import common.httpClient
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

val url = "http://localhost:5001/stress1/us-central1/startAtLauncher" // Local Emu.
//val url = "https://us-central1-stress1.cloudfunctions.net/startAtLauncher" // Cloud★★★

// .../startAtLauncher/?id=<dev_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>

@Serializable
data class Request(val id: String, val nr: Int, val nm: Int, val ts: Long) {
    fun url() = "$url?id=$id&nr=$nr&nm=$nm&ts=$ts"
}

@Serializable
data class Result(val res: List<String>)

@ExperimentalTime
suspend fun main(args: Array<String>): Unit = runBlocking {
    val (nReq, nMsg) = args.map { it.toInt() }
    val tOrg = now()
    val tStart = tOrg + 5 * 1000
    (0 until nReq).map { id ->
        fun now() = Clock.System.now().toEpochMilliseconds()
        val req = Request("$id", nReq, nMsg, tStart)
        println("$id ${now() - tOrg} ${req.url()}")
        async { httpClient.get<Result>(req.url()) }
    }.awaitAll().forEach {  println(it) }

}

fun now() = Clock.System.now().toEpochMilliseconds()

