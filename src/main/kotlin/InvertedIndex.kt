import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

data class Posting(val docId: String, val positions: MutableList<Int>)

class InvertedIndex {
    private val index: MutableMap<String, MutableList<Posting>> = mutableMapOf()
    private val lock = ReentrantReadWriteLock()

    fun addTerm(term: String, docId: String, position: Int) {
        lock.write {
            val postings = index.getOrPut(term) { mutableListOf() }
            val posting = postings.find { it.docId == docId }

            posting?.positions?.add(position) ?: postings.add(Posting(docId, mutableListOf(position)))
        }
    }

    fun getPostings(term: String): List<Posting>? {
        return index[term]
    }

    fun displayIndex() {
        for ((term, postings) in index) {
            println("Term: \"$term\"")
            postings.forEach { posting ->
                println("   DocID: ${posting.docId}, Positions: ${posting.positions}")
            }
        }
    }
}