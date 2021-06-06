import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.*
import org.joda.time.DateTime  // https://mvnrepository.com/artifact/joda-time/joda-time
import java.util.concurrent.TimeUnit

import kotlin.time.ExperimentalTime
import kotlin.time.*

@ExperimentalCoroutinesApi
@ExperimentalTime
@InternalCoroutinesApi
fun main(args: Array<String>) = runBlocking {
    val ts = Clock.System.now()
    runCatching {
        val cmd = arrayOf("cmd.exe", "/c", "firebase functions:log ${args.joinToString(" ")}")
        channelFlow {
            Runtime.getRuntime().exec(cmd, null, File("."))!!.inputStream.bufferedReader().forEachLine { offer(it) }
        }
            .filter { it.contains("id:") }
            .filter { it.contains("1623018780624") }
            .collectIndexed { i, it ->
                val strDate = it.split(" ")[0]
                val t = DateTime(strDate)
                val t2 = Instant.parse(strDate)
                val x = ts - t2
                println("$i, ${x.toLong(TimeUnit.MILLISECONDS)}, ${x.toIsoString()}, $it")
            }
    }.onFailure { it.printStackTrace() }.getOrThrow()
}