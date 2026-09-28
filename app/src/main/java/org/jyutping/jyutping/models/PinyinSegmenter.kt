package org.jyutping.jyutping.models

import android.util.Log
import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.presets.PresetString

data class PinyinSyllable(
        val code: Long,
        val text: String
) : Comparable<PinyinSyllable> {
        val keys: List<VirtualInputKey> = code.matchedVirtualInputKeys

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is PinyinSyllable) return false
                return code == other.code
        }
        override fun hashCode(): Int = code.hashCode()
        override fun compareTo(other: PinyinSyllable): Int {
                val quotient = this.code / other.code
                return if (quotient > 0L) -1 else 1
        }
}

typealias PinyinScheme = List<PinyinSyllable>
typealias PinyinSegmentation = List<PinyinScheme>

/** Count of all input keys */
val PinyinScheme.schemeLength: Int
        get() = this.fold(0) { acc, syllable -> acc + syllable.keys.size }

/**
 * Conjoined digit of syllable lengths.
 *
 * For example: lengths of syllables “xi an shi” are `[2, 2, 3]`, which makes the `complexity` become `223`
 */
val PinyinScheme.complexity: Long
        get() = this.map { it.keys.size }.decimalOverflowed()

/** Input keys conjoined as a sequence */
val PinyinScheme.keys: List<VirtualInputKey>
        get() = this.flatMap { it.keys }

/** Syllable texts separated by spaces */
val PinyinScheme.previewMark: String
        get() = this.joinToString(separator = PresetString.SPACE) { it.text }

/** Segments Pinyin input into possible syllable schemes. */
object PinyinSegmenter {

        private const val TAG: String = "org.jyutping.jyutping.PinyinSegmenter"

        fun prepare() {
                if (pinyinSyllableMap.isEmpty()) {
                        Log.w(TAG, "PinyinSyllable Dictionary is Empty")
                }
        }

        private val pinyinSyllableMap: Map<Long, PinyinSyllable> by lazy {
                val dict: HashMap<Long, PinyinSyllable> = HashMap(500)
                val command: String = "SELECT code, syllable FROM syllable_pinyin_table;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val code = cursor.getLong(0)
                                val syllable = cursor.getString(1) ?: continue
                                dict[code] = PinyinSyllable(code = code, text = syllable)
                        }
                }
                dict
        }
        private fun lookup(code: Long): PinyinSyllable? = pinyinSyllableMap[code]

        private const val maxSyllableKeyCount: Int = 6

        private class SplitEdge(val syllable: PinyinSyllable, val endIndex: Int)
        private class SplitNode(val syllable: PinyinSyllable, val previousIndex: Int, val length: Int)

        private fun splitEdges(keys: List<VirtualInputKey>): Array<MutableList<SplitEdge>> {
                val inputLength = keys.size
                val edges = Array(inputLength) { mutableListOf<SplitEdge>() }
                for (startIndex in 0 until inputLength) {
                        var code: Long = 0L
                        val endIndexLimit = minOf(inputLength, startIndex + maxSyllableKeyCount)
                        for (endIndex in startIndex until endIndexLimit) {
                                code = code * 100L + keys[endIndex].code
                                val syllable = lookup(code) ?: continue
                                edges[startIndex].add(SplitEdge(syllable = syllable, endIndex = endIndex + 1))
                        }
                }
                return edges
        }

        private fun scheme(nodeIndex: Int, nodes: List<SplitNode>): PinyinScheme {
                val syllables: MutableList<PinyinSyllable> = ArrayList(nodes[nodeIndex].length)
                var currentIndex: Int = nodeIndex
                while (currentIndex >= 0) {
                        val node = nodes[currentIndex]
                        syllables.add(node.syllable)
                        currentIndex = node.previousIndex
                }
                syllables.reverse()
                return syllables
        }

        private fun split(keys: List<VirtualInputKey>): PinyinSegmentation {
                val inputLength = keys.size
                if (inputLength <= 0) return emptyList()
                val edges = splitEdges(keys)
                if (edges.firstOrNull()?.isEmpty() != false) return emptyList()
                val nodes: MutableList<SplitNode> = mutableListOf()
                val nodeIndicesByLength = Array(inputLength + 1) { mutableListOf<Int>() }
                for (edge in edges[0]) {
                        val node = SplitNode(syllable = edge.syllable, previousIndex = -1, length = edge.endIndex)
                        nodes.add(node)
                        nodeIndicesByLength[node.length].add(nodes.size - 1)
                }
                var levelStartIndex = 0
                var levelEndIndex = nodes.size
                while (levelStartIndex < levelEndIndex) {
                        val nextLevelStartIndex = levelEndIndex
                        for (nodeIndex in levelStartIndex until levelEndIndex) {
                                val node = nodes[nodeIndex]
                                if (node.length >= inputLength) continue
                                for (edge in edges[node.length]) {
                                        val nextNode = SplitNode(syllable = edge.syllable, previousIndex = nodeIndex, length = edge.endIndex)
                                        nodes.add(nextNode)
                                        nodeIndicesByLength[nextNode.length].add(nodes.size - 1)
                                }
                        }
                        levelStartIndex = nextLevelStartIndex
                        levelEndIndex = nodes.size
                }
                val schemes: MutableList<PinyinScheme> = ArrayList(nodes.size)
                for (length in inputLength downTo 1) {
                        for (nodeIndex in nodeIndicesByLength[length]) {
                                schemes.add(scheme(nodeIndex = nodeIndex, nodes = nodes))
                        }
                }
                return schemes
        }

        fun segment(keys: List<VirtualInputKey>): PinyinSegmentation {
                return split(keys.filter { it.isLetter })
        }
}
