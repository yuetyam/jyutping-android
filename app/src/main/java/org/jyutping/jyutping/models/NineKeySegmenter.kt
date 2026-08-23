package org.jyutping.jyutping.models

import android.util.Log
import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.ninekey.Combo
import org.jyutping.jyutping.ninekey.matchedCombos

/** A Jyutping syllable represented by its 9-key alias and origin sequences. */
class NineKeySyllable(
        val aliasCode: Long,
        val originCode: Long,
        val serialAliasCode: Long,
        val serialOriginCode: Long
) : Comparable<NineKeySyllable> {

        val alias: List<Combo> = aliasCode.matchedCombos
        val origin: List<Combo> = originCode.matchedCombos
        val serialAlias: List<VirtualInputKey> = serialAliasCode.matchedVirtualInputKeys
        val serialOrigin: List<VirtualInputKey> = serialOriginCode.matchedVirtualInputKeys

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is NineKeySyllable) return false
                return (aliasCode == other.aliasCode) && (originCode == other.originCode)
        }
        override fun hashCode(): Int {
                return aliasCode.hashCode() * 31 + originCode.hashCode()
        }
        override fun compareTo(other: NineKeySyllable): Int {
                val aliasQuotient = this.aliasCode / other.aliasCode
                if (aliasQuotient != 0L) return -1
                val originQuotient = this.originCode / other.originCode
                return if (originQuotient > 0L) -1 else 1
        }

        override fun toString(): String = "NineKeySyllable(alias=${alias.map { it.digit }},origin=${origin.map { it.digit }})"
}

val NineKeySyllable.isRegular: Boolean
        get() = (aliasCode == originCode)
val NineKeySyllable.isIrregular: Boolean
        get() = (aliasCode != originCode)

typealias NineKeyScheme = List<NineKeySyllable>
typealias NineKeySegmentation = List<NineKeyScheme>

/** Count of all alias combos */
val NineKeyScheme.length: Int
        get() = this.fold(0) { acc, syllable -> acc + syllable.alias.size }

/**
 * Conjoined digit of syllable origin lengths.
 *
 * For example: lengths of syllables “gwong dung dou” are `[5, 4, 3]`, which makes the `complexity` become `543`
 */
val NineKeyScheme.complexity: Long
        get() = this.map { it.origin.size }.decimalOverflowed()

/** Alias combos conjoined as a sequence */
val NineKeyScheme.aliasCombos: List<Combo>
        get() = this.flatMap { it.alias }

/** Origin combos conjoined as a sequence */
val NineKeyScheme.originCombos: List<Combo>
        get() = this.flatMap { it.origin }

/** Serial origin keys conjoined as a sequence */
val NineKeyScheme.serialOriginKeys: List<VirtualInputKey>
        get() = this.flatMap { it.serialOrigin }

/** Segments 9-key input into possible Jyutping syllable schemes. */
object NineKeySegmenter {

        private const val TAG: String = "NineKeySegmenter"

        fun prepare() {
                if (syllableCodeMap.isEmpty()) {
                        Log.w(TAG, "NineKeySyllable Dictionary is Empty")
                }
        }

        private val syllableCodeMap: Map<Long, NineKeySyllable> by lazy {
                val dict: HashMap<Long, NineKeySyllable> = HashMap(700)
                val command: String = "SELECT alias_code, origin_code, alias_9key_code, origin_9key_code FROM syllable_9key_table ORDER BY rowid;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val serialAliasCode = cursor.getLong(0)
                                val serialOriginCode = cursor.getLong(1)
                                val nineKeyAliasCode = cursor.getLong(2)
                                val nineKeyOriginCode = cursor.getLong(3)
                                val syllable = NineKeySyllable(aliasCode = nineKeyAliasCode, originCode = nineKeyOriginCode, serialAliasCode = serialAliasCode, serialOriginCode = serialOriginCode)
                                val stored = dict[nineKeyAliasCode]
                                if (stored != null) {
                                        if (stored.isIrregular && syllable.isRegular) {
                                                dict[nineKeyAliasCode] = syllable
                                        }
                                } else {
                                        dict[nineKeyAliasCode] = syllable
                                }
                        }
                }
                dict
        }
        private fun lookup(code: Long): NineKeySyllable? = syllableCodeMap[code]

        private const val maxSyllableComboCount: Int = 6

        private class SplitEdge(val syllable: NineKeySyllable, val endIndex: Int)
        private class SplitNode(val syllable: NineKeySyllable, val previousIndex: Int, val length: Int)

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

        private fun scheme(nodeIndex: Int, nodes: List<SplitNode>): NineKeyScheme {
                val syllables: MutableList<NineKeySyllable> = ArrayList(nodes[nodeIndex].length)
                var currentIndex: Int = nodeIndex
                while (currentIndex >= 0) {
                        val node = nodes[currentIndex]
                        syllables.add(node.syllable)
                        currentIndex = node.previousIndex
                }
                syllables.reverse()
                return syllables
        }

        private fun split(combos: List<Combo>): NineKeySegmentation {
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
                val schemes: MutableList<NineKeyScheme> = ArrayList(nodes.size)
                for (length in inputLength downTo 1) {
                        for (nodeIndex in nodeIndicesByLength[length]) {
                                schemes.add(scheme(nodeIndex = nodeIndex, nodes = nodes))
                        }
                }
                return schemes
        }

        /** Returns possible syllable schemes ordered by consumed input length, then syllable count. */
        fun segment(combos: List<Combo>): NineKeySegmentation {
                return split(combos)
        }
}
