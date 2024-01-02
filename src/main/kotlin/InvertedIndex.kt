import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class Posting(val docId: String, val positions: MutableList<Int>)

class InvertedIndex(private val shardCount: Int = 20) {
    private val shards: Array<HashMap<String, MutableList<Posting>>> = Array(shardCount) { hashMapOf() }
    private val locks: Array<ReentrantLock> = Array(shardCount) { ReentrantLock() }

    private fun getShardIndex(key: String): Int {
        return (key.hashCode() and Int.MAX_VALUE) % shardCount
    }

    fun addTerm(term: String, docId: String, position: Int) {
        val shardIndex = getShardIndex(term)
        val shard = shards[shardIndex]
        val lock = locks[shardIndex]

        lock.withLock {
            val postings = shard.getOrPut(term) { mutableListOf() }
            val posting = postings.find { it.docId == docId }

            posting?.positions?.add(position) ?: postings.add(Posting(docId, mutableListOf(position)))
        }
    }

    fun searchDocuments(words: List<String>): Set<String> {
        if (words.isEmpty()) return emptySet()

        val initialPostings = getPostings(words.first()) ?: return emptySet()

        // Map to track the last valid positions for each document
        var validPostings = initialPostings.associate { it.docId to it.positions.toMutableSet() }

        for (word in words.drop(1)) {
            val postings = getPostings(word) ?: return emptySet()

            val newValidPostings = mutableMapOf<String, MutableSet<Int>>()

            // Update the map for the next valid positions
            validPostings.forEach { (docId, positions) ->
                val newPositions = postings.find { it.docId == docId }?.positions
                newPositions?.let {
                    val validPositions = positions.map { it + 1 }.intersect(newPositions.toSet())
                    if (validPositions.isNotEmpty()) {
                        newValidPostings[docId] = validPositions.toMutableSet()
                    }
                }
            }

            // Update the validPostings for the next iteration
            validPostings = newValidPostings.ifEmpty { return emptySet() }
        }

        return validPostings.keys
    }

    private fun getPostings(term: String): List<Posting>? {
        val shardIndex = getShardIndex(term)
        val shard = shards[shardIndex]

        return shard[term]?.toList()
    }

    fun getSize(): Int {
        var totalTerms = 0
        locks.forEachIndexed { index, lock ->
            lock.withLock {
                totalTerms += shards[index].size
            }
        }
        return totalTerms
    }
}
