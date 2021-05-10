package startAt

import common.httpClient
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

val url = "http://localhost:5001/stress1/us-central1/startAt" // Local Emu.
//val url = "https://us-central1-stress1.cloudfunctions.net/startAt" // Cloud★★★

@Serializable
data class Request(val devId: String, val n: Int, val et: Long, val st: Long) {
    fun url() = "$url?id=$devId&n=$n&et=$et&st=$st"
}

@Serializable
data class Result(val devId: String, val et: Long, val ct: Long, val st: Long, val end: Long, val res: List<String>)

@ExperimentalTime
suspend fun main(args: Array<String>): Unit = runBlocking {
    val (nDev, nMsg) = args.map { it.toInt() }

    val org = now()
    val st = org + 30 * 1000
    (0 until nDev).map { id ->
        delay(10)
        val req = Request("$id", nMsg, now(), st)
        println("$id ${now() - org} ${req.url()}")
        async {
            httpClient.get<Result>(req.url())
        }
    }.awaitAll().forEach { res ->
        //println("${it.devId}, ${it.et}, ${it.ct}, ${it.st}, ${it.end}, ${it.end - it.st}, $it")
        res.res.forEach { println(it) }
    }
}

fun now() = Clock.System.now().toEpochMilliseconds()

