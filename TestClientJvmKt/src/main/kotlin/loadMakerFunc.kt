package loadMakerFunc

import common.httpClient
import common.urlBase
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class Request(val id: String, val nReq: Int, val tCall: String) {
    fun url(nRound: Int) = "$urlBase/loadMakerFuncs?id=$id&fn=$nRound&nr=$nReq&tc=$tCall"
}

@Serializable
data class Result(val id: String, val tc: Long, val te: Long, val cr: Long)

@ExperimentalTime
fun main(args: Array<String>): Unit = runBlocking {
    val (nReq, nRound, nMsg) = args.map { it.toInt() }
    val org = now()

    //val sem = Semaphore(50)
    val rs = (0 until nReq).map { i ->
        async {
            val req = Request("${i}R${i % nRound}", nMsg, "main,${now()}")
            println("$i ${now() - org}, ${req.url(i % nRound)}")
            val url = req.url(i % nRound)
            httpClient.get<Result>(url)
        }
    }.awaitAll().map { it.also { println(it) } }.sumOf { it.cr }
    println("total num of results: $rs")
    println("total time required: ${now() - org}")
}

fun now() = Clock.System.now().toEpochMilliseconds()

