import java.io.File
import java.util.*

fun getAllTextFiles(directoryPath: String): List<File> {
    val directory = File(directoryPath)
    return if (directory.exists() && directory.isDirectory) {
        directory.walk().filter { it.isFile && it.extension == "txt" }.toList()
    } else {
        emptyList()
    }
}

fun splitToWords(content: String) = content.split("\\s+".toRegex()) // Splitting by whitespace

fun tokenizeWord(word: String) = word.lowercase(Locale.getDefault()).filter { it.isLetterOrDigit() } // Lowercase and remove punctuation