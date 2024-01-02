import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.Scanner

class Client(serverAddress: String, port: Int) {
    private val socket = Socket(serverAddress, port)
    private val dis: DataInputStream = DataInputStream(socket.getInputStream())
    private val dos: DataOutputStream = DataOutputStream(socket.getOutputStream())
    private val scanner = Scanner(System.`in`)
    fun start() {
        try {
            var response: String

            while (true) {
                response = dis.readUTF()
                println(response)
                if (response == "Closing this connection.") {
                    break
                }
                if (response == "Waiting for command..." || response == "Enter search query.") {
                    dos.writeUTF(scanner.nextLine())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            stop()
        }
    }

    fun stop() {
        dos.close()
        dis.close()
        socket.close()
    }
}

fun main() {
    val client = Client("localhost", 8080)
    client.start()
}