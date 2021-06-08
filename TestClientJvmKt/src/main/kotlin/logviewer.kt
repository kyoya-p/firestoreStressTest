import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.io.*
import org.joda.time.DateTime  // https://mvnrepository.com/artifact/joda-time/joda-time
import java.util.concurrent.TimeUnit

import kotlin.time.ExperimentalTime
import kotlin.time.*

@ExperimentalCoroutinesApi
@ExperimentalTime
@InternalCoroutinesApi
fun xxx(args: Array<String>) = runBlocking {
    val ts = Clock.System.now()
    runCatching {
        val cmd = arrayOf("cmd.exe", "/c", "firebase functions:log ${args.joinToString(" ")}")
        channelFlow {
            Runtime.getRuntime().exec(cmd, null, File("."))!!.inputStream.bufferedReader().forEachLine { offer(it) }
        }
            .filter { it.contains("id:") }
            .collectIndexed { i, it ->
                val strDate = it.split(" ")[0]
                val t = DateTime(strDate)
                val t2 = Instant.parse(strDate)
                val x = ts - t2
                println("$i, ${x.toLong(TimeUnit.MILLISECONDS)}, ${x.toIsoString()}, $it")
            }
    }.onFailure { it.printStackTrace() }.getOrThrow()
}

@Serializable
data class Src(val time: String, val errLv: String, val fName: String, val msg: String)

fun <T> T?.ifNull(op: Any?.() -> Unit): T? {
    if (this == null) op()
    return this
}

fun main() {
    File("TestClientJvmKt/logs/funcLog.txt").bufferedReader().lineSequence().mapNotNull { line ->
        val re = Regex("""^(.*?)\s+(.*?)\s+(.*?):\s+(.*)$""")
        re.matchEntire(line)?.groupValues?.ifNull { println("Format Error: $line") }
    }.map { Src(it[1], it[2], it[3], it[4]) }
        .filter { it.msg.contains("id:") }
        .forEachIndexed { i, e ->
            val time = DateTime(e.time).toDate()
            println("$i, ${time.time}, ${time}, ${e.fName}, ${e.msg}")
        }
}
