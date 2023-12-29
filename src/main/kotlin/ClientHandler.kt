import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class ClientHandler(private val socket: Socket) : Runnable {
    private val dis: DataInputStream = DataInputStream(socket.getInputStream())
    private val dos: DataOutputStream = DataOutputStream(socket.getOutputStream())

    private val isConstructed = AtomicBoolean(false)
    private var isTerminated = false

    override fun run() {
        try {
            var received: String

            dos.writeUTF("""
                Welcome to the Oxygen Search Engine!
                - Type 'SEARCH' to start data search.
                - Type 'EXIT' to terminate connection. 
            """.trimIndent())

            while (!isTerminated) {
                dos.writeUTF("Waiting for command...")

                received = dis.readUTF()
                when (received.uppercase()) {
                    "SEARCH" -> {
                        dos.writeUTF(if (!isConstructed.get()) "The system isn't ready. Try again later."
                                     else "Enter words to search.")
                        // code to retrieve data from index
                    }
                    "EXIT" -> {
                        isTerminated = true
                        dos.writeUTF("Closing this connection.")
                    }
                    else-> dos.writeUTF("Unexpected command.")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            socket.close()
            dis.close()
            dos.close()
        }
    }
}
