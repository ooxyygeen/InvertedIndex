import java.net.ServerSocket
import kotlin.concurrent.thread

fun main() {
    val serverSocket = ServerSocket(8080)

    while (true) {
        val clientSocket = serverSocket.accept()
        println("client accepted $clientSocket")
        val client = ClientHandler(clientSocket)
        thread {
            client.run()
        }
    }
}
