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

    fun getPostings(term: String): List<Posting>? {
        val shardIndex = getShardIndex(term)
        val shard = shards[shardIndex]
        val lock = locks[shardIndex]

        return lock.withLock {
            shard[term]?.toList()
        }
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
