import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import java.io.*

@InternalCoroutinesApi
fun main(args: Array<String>) = runBlocking {
    runCatching {
        val cmd = arrayOf("cmd.exe", "/c", "firebase functions:log -n 201")
        channelFlow {
            Runtime.getRuntime().exec(cmd, null, File("."))!!.inputStream.bufferedReader().forEachLine { offer(it) }
        }
            .filter { it.contains("Function execution started") }
            .collect {
                println(it)
            }
    }.onFailure { it.printStackTrace() }.getOrThrow()
}