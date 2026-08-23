package org.jyutping.jyutping.models

import android.util.Log
import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.ninekey.Combo
import org.jyutping.jyutping.ninekey.matchedCombos

data class PinyinNineKeySyllable(
        val code: Long
) {
        val combos: List<Combo> = code.matchedCombos

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is PinyinNineKeySyllable) return false
                return code == other.code
        }
        override fun hashCode(): Int = code.hashCode()
}

typealias PinyinNineKeyScheme = List<PinyinNineKeySyllable>
typealias PinyinNineKeySegmentation = List<PinyinNineKeyScheme>

/** Count of all input combos */
val PinyinNineKeyScheme.length: Int
        get() = this.fold(0) { acc, syllable -> acc + syllable.combos.size }

/**
 * Conjoined digit of syllable lengths.
 *
 * For example: lengths of syllables “xi an shi” are `[2, 2, 3]`, which makes the `complexity` become `223`
 */
val PinyinNineKeyScheme.complexity: Long
        get() = this.map { it.combos.size }.decimalOverflowed()

/** Input combos conjoined as a sequence */
val PinyinNineKeyScheme.combos: List<Combo>
        get() = this.flatMap { it.combos }

/** Segments 9-key Pinyin input into possible syllable schemes. */
object PinyinNineKeySegmenter {

        private const val TAG: String = "PinyinNineKeySegmenter"

        fun prepare() {
                if (syllableCodeMap.isEmpty()) {
                        Log.w(TAG, "PinyinNineKeySyllable Dictionary is Empty")
                }
        }

        private val syllableCodeMap: Map<Long, PinyinNineKeySyllable> by lazy {
                val dict: HashMap<Long, PinyinNineKeySyllable> = HashMap(500)
                val command: String = "SELECT DISTINCT code_9key FROM syllable_pinyin_table ORDER BY code_9key;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val code = cursor.getLong(0)
                                dict[code] = PinyinNineKeySyllable(code = code)
                        }
                }
                dict
        }
        private fun lookup(code: Long): PinyinNineKeySyllable? = syllableCodeMap[code]

        private const val maxSyllableComboCount: Int = 6

        private class SplitEdge(val syllable: PinyinNineKeySyllable, val endIndex: Int)
        private class SplitNode(val syllable: PinyinNineKeySyllable, val previousIndex: Int, val length: Int)

        private fun splitEdges(combos: List<Combo>): Array<MutableList<SplitEdge>> {
                val inputLength = combos.size
                val edges = Array(inputLength) { mutableListOf<SplitEdge>() }
                for (startIndex in 0 until inputLength) {
                        var code: Long = 0L
                        val endIndexLimit = minOf(inputLength, startIndex + maxSyllableComboCount)
                        for (endIndex in startIndex until endIndexLimit) {
                                code = code * 10L + combos[endIndex].digit
                                val syllable = lookup(code) ?: continue
                                edges[startIndex].add(SplitEdge(syllable = syllable, endIndex = endIndex + 1))
                        }
                }
                return edges
        }

        private fun scheme(nodeIndex: Int, nodes: List<SplitNode>): PinyinNineKeyScheme {
                val syllables: MutableList<PinyinNineKeySyllable> = ArrayList(nodes[nodeIndex].length)
                var currentIndex: Int = nodeIndex
                while (currentIndex >= 0) {
                        val node = nodes[currentIndex]
                        syllables.add(node.syllable)
                        currentIndex = node.previousIndex
                }
                syllables.reverse()
                return syllables
        }

        private fun split(combos: List<Combo>): PinyinNineKeySegmentation {
                val inputLength = combos.size
                if (inputLength <= 0) return emptyList()
                val edges = splitEdges(combos)
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
                val schemes: MutableList<PinyinNineKeyScheme> = ArrayList(nodes.size)
                for (length in inputLength downTo 1) {
                        for (nodeIndex in nodeIndicesByLength[length]) {
                                schemes.add(scheme(nodeIndex = nodeIndex, nodes = nodes))
                        }
                }
                return schemes
        }

        fun segment(combos: List<Combo>): PinyinNineKeySegmentation {
                return split(combos)
        }
}
