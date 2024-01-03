import java.net.ServerSocket
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val threadNum = if (args.isNotEmpty()) {
        try {
            args[0].toInt()
        } catch (e: NumberFormatException) {
            1
        }
    } else {
        1
    }
    val serverSocket = ServerSocket(8080)
    val index = InvertedIndex()
    val files = getAllTextFiles("dataset")
    val isCompleted = AtomicBoolean(false)

    thread(start = true) { Indexer(files, threadNum, isCompleted, index).run() }

    val clientThreadPool = Executors.newCachedThreadPool()

    while (true) {
        val clientSocket = serverSocket.accept()
        println("New client accepted $clientSocket")
        val client = ClientHandler(clientSocket, index, isCompleted)
        clientThreadPool.execute {
            client.run()
        }
    }
}
