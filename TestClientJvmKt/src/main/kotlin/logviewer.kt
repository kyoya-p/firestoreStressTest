import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.*
import org.joda.time.DateTime  // https://mvnrepository.com/artifact/joda-time/joda-time

import kotlin.time.ExperimentalTime
import kotlin.time.*

@ExperimentalTime
@InternalCoroutinesApi
fun main(args: Array<String>) = runBlocking {
    val ts = Clock.System.now()
    runCatching {
        val cmd = arrayOf("cmd.exe", "/c", "firebase functions:log -n 606")
        channelFlow {
            Runtime.getRuntime().exec(cmd, null, File("."))!!.inputStream.bufferedReader().forEachLine { offer(it) }
        }
            //.filter { it.contains("success") }
            .collectIndexed { i, it ->
                val strDate = it.split(" ")[0]
                val t = DateTime(strDate)
                val t2 = Instant.parse(strDate)
                val x = ts - t2
                println("$i, ${x.toIsoString()}, $it")
            }
    }.onFailure { it.printStackTrace() }.getOrThrow()
}