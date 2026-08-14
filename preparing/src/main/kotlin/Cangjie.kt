package org.jyutping.preparing

import kotlin.math.max

data class CangjieEntry(
        val word: String,
        val cangjie5: String,
        val cangjie3: String,
        val c5complex: Int,
        val c3complex: Int,
        val c5code: Long,
        val c3code: Long,
)

object Cangjie {
        fun generate(): List<CangjieEntry> {
                val characters = LexiconConverter.jyutpingSourceLines.mapNotNull { line ->
                        val word = line.substringBefore(PresetString.TAB).trim()
                        word.takeIf { it.characterCount() == 1 }
                }.distinct()
                return characters.flatMap { word ->
                        val cangjie5Values = matchCangjie5(word)
                        val cangjie3Values = matchCangjie3(word)
                        if (cangjie5Values.isEmpty() && cangjie3Values.isEmpty()) {
                                return@flatMap emptyList()
                        }
                        (0 until max(cangjie5Values.size, cangjie3Values.size)).map { index ->
                                val cangjie5 = cangjie5Values.getOrNull(index) ?: "X"
                                val cangjie3 = cangjie3Values.getOrNull(index) ?: "X"
                                CangjieEntry(
                                        word = word,
                                        cangjie5 = cangjie5,
                                        cangjie3 = cangjie3,
                                        c5complex = cangjie5.length,
                                        c3complex = cangjie3.length,
                                        c5code = cangjie5.serialCode,
                                        c3code = cangjie3.serialCode,
                                )
                        }
                }.distinct()
        }

        fun matchCangjie5(word: String): List<String> = cangjie5Map[word].orEmpty()

        fun matchCangjie3(word: String): List<String> = cangjie3Map[word].orEmpty()

        private val cangjie5Map: Map<String, List<String>> by lazy { sourceMap("cangjie5.txt") }
        private val cangjie3Map: Map<String, List<String>> by lazy { sourceMap("cangjie3.txt") }

        private fun sourceMap(fileName: String): Map<String, List<String>> {
                return readResourceLines(fileName).fold(mutableMapOf<String, MutableList<String>>()) { result, line ->
                        val parts = line.split(PresetString.TAB)
                        if (parts.size == 2) {
                                result.getOrPut(parts[0]) { mutableListOf() }.add(parts[1])
                        }
                        result
                }
        }
}
