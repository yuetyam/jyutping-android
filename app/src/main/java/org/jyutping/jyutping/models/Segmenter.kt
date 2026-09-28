package org.jyutping.jyutping.models

import android.util.Log
import org.jyutping.jyutping.Elephant

/** Segments Jyutping input into possible syllable schemes. */
object Segmenter {

        private const val TAG: String = "org.jyutping.jyutping.Segmenter"

        fun prepare() {
                if (syllableCodeMap.isEmpty()) {
                        Log.w(TAG, "Syllable Dictionary is Empty")
                }
        }

        private val syllableCodeMap: Map<Long, Syllable> by lazy {
                val dict: HashMap<Long, Syllable> = HashMap(1300)
                val command: String = "SELECT alias_code, origin_code FROM syllable_core_table;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val aliasCode = cursor.getLong(0)
                                val originCode = cursor.getLong(1)
                                dict[aliasCode] = Syllable(aliasCode = aliasCode, originCode = originCode)
                        }
                }
                dict
        }
        private fun lookup(code: Long): Syllable? = syllableCodeMap[code]

        private const val MAX_SYLLABLE_KEY_COUNT: Int = 6

        private class SplitEdge(val syllable: Syllable, val endIndex: Int)
        private class SplitNode(val syllable: Syllable, val previousIndex: Int, val length: Int)

        private fun splitEdges(keys: List<VirtualInputKey>): Array<MutableList<SplitEdge>> {
                val inputLength = keys.size
                val edges = Array(inputLength) { mutableListOf<SplitEdge>() }
                for (startIndex in 0 until inputLength) {
                        var code: Long = 0L
                        val endIndexLimit = minOf(inputLength, startIndex + MAX_SYLLABLE_KEY_COUNT)
                        for (endIndex in startIndex until endIndexLimit) {
                                code = code * 100L + keys[endIndex].code
                                val syllable = lookup(code) ?: continue
                                edges[startIndex].add(SplitEdge(syllable = syllable, endIndex = endIndex + 1))
                        }
                }
                return edges
        }

        private fun scheme(nodeIndex: Int, nodes: List<SplitNode>): Scheme {
                val syllables: MutableList<Syllable> = ArrayList(nodes[nodeIndex].length)
                var currentIndex: Int = nodeIndex
                while (currentIndex >= 0) {
                        val node = nodes[currentIndex]
                        syllables.add(node.syllable)
                        currentIndex = node.previousIndex
                }
                syllables.reverse()
                return syllables
        }

        private fun split(keys: List<VirtualInputKey>): Segmentation {
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
                val schemes: MutableList<Scheme> = ArrayList(nodes.size)
                for (length in inputLength downTo 1) {
                        for (nodeIndex in nodeIndicesByLength[length]) {
                                schemes.add(scheme(nodeIndex = nodeIndex, nodes = nodes))
                        }
                }
                return schemes
        }

        fun segment(keys: List<VirtualInputKey>): Segmentation {
                return when (keys.size) {
                        0 -> emptyList()
                        1 -> when (keys.first()) {
                                VirtualInputKey.letterA -> letterA
                                VirtualInputKey.letterO -> letterO
                                VirtualInputKey.letterM -> letterM
                                else -> emptyList()
                        }
                        else -> split(keys.filter { it.isSyllableLetter })
                }
        }

        private val letterA: Segmentation = listOf(listOf(Syllable(aliasCode = 20L, originCode = 2020L)))
        private val letterO: Segmentation = listOf(listOf(Syllable(aliasCode = 34L, originCode = 34L)))
        private val letterM: Segmentation = listOf(listOf(Syllable(aliasCode = 32L, originCode = 32L)))

        fun syllableText(keys: List<VirtualInputKey>): String? {
                if (keys.size > 6) return null
                return lookup(keys.conjoinedCode)?.originText
        }
}
