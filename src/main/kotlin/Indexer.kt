import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class Indexer(private val files: List<File>,
              private val numThreads: Int,
              private val isCompleted: AtomicBoolean,
              private val invertedIndex: InvertedIndex) : Runnable {
    override fun run() {
        val startTime = System.nanoTime()

        val threads = mutableListOf<Thread>()
        for (i in 0 until numThreads) {
            val thread = thread(start = true) {
                val start = files.size / numThreads * i
                val end = if (i == numThreads - 1) files.size else files.size / numThreads * (i + 1)
                val sublist = files.subList(start, end)
                constructIndex(sublist)
            }
            threads.add(thread)
        }

        for (thread in threads) {
            thread.join()
        }

        isCompleted.set(true)

        val endTime = System.nanoTime()
        val timeTaken = endTime - startTime
        println("Time taken for index construction: ${timeTaken / 1_000_000.0} ms")

    }

    private fun constructIndex(sublist: List<File>) {
        sublist.forEach { file ->
            file.bufferedReader().use { reader ->
                var position = 0
                while (true) {
                    val line = reader.readLine() ?: break
                    val words = splitToWords(line)
                    words.forEach { word ->
                        val normalizedWord = tokenizeWord(word)
                        if (normalizedWord.isNotEmpty()) {
                            invertedIndex.addTerm(normalizedWord, file.name, position++)
                        }
                    }
                }
            }
        }
    }
}
