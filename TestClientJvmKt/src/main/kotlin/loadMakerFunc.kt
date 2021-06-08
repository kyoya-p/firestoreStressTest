package loadMakerFunc

import common.httpClient
import common.urlBase
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

suspend fun loadMakerFuncs(id: String, nReq: Int, tCall: String, nRound: Int) = httpClient.get<Result>(
    "$urlBase/loadMakerFuncs?id=$id&fn=$nRound&nr=$nReq&tc=$tCall"
)

@Serializable
data class Result(val id: String, val tc: Long, val te: Long, val cr: Long)

fun now() = Clock.System.now().toEpochMilliseconds()
val org = now()

@ExperimentalTime
fun main(): Unit = runBlocking {
    println("org: $org")
    loadMakerFuncs(id = "$org", nReq = 100, tCall = "no", nRound = 1)
    println("total time required: ${now() - org}")
}


