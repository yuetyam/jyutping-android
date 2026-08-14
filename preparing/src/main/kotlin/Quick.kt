package org.jyutping.preparing

import kotlin.math.max

data class QuickEntry(
        val word: String,
        val quick5: String,
        val quick3: String,
        val q5complex: Int,
        val q3complex: Int,
        val q5code: Long,
        val q3code: Long,
)

object Quick {
        fun generate(): List<QuickEntry> {
                val words = LexiconConverter.jyutpingSourceLines.map { line ->
                        line.substringBefore(PresetString.TAB).trim()
                }.distinct()
                return words.flatMap { word ->
                        if (word.characterCount() == 1) {
                                singleCharacterEntries(word)
                        } else {
                                listOf(compoundEntry(word))
                        }
                }.distinct()
        }

        private fun singleCharacterEntries(word: String): List<QuickEntry> {
                val cangjie5Values = Cangjie.matchCangjie5(word)
                val cangjie3Values = Cangjie.matchCangjie3(word)
                if (cangjie5Values.isEmpty() && cangjie3Values.isEmpty()) return emptyList()
                return (0 until max(cangjie5Values.size, cangjie3Values.size)).map { index ->
                        entry(
                                word = word,
                                cangjie5 = cangjie5Values.getOrNull(index) ?: "X",
                                cangjie3 = cangjie3Values.getOrNull(index) ?: "X",
                        )
                }
        }

        private fun compoundEntry(word: String): QuickEntry {
                val characters = word.codePoints().toArray().map { String(Character.toChars(it)) }
                val quick5 = characters.joinToString(PresetString.EMPTY) {
                        quickCode(Cangjie.matchCangjie5(it).firstOrNull() ?: "X")
                }
                val quick3 = characters.joinToString(PresetString.EMPTY) {
                        quickCode(Cangjie.matchCangjie3(it).firstOrNull() ?: "X")
                }
                return quickEntry(word = word, quick5 = quick5, quick3 = quick3)
        }

        private fun entry(word: String, cangjie5: String, cangjie3: String): QuickEntry {
                return quickEntry(word = word, quick5 = quickCode(cangjie5), quick3 = quickCode(cangjie3))
        }

        private fun quickEntry(word: String, quick5: String, quick3: String): QuickEntry {
                return QuickEntry(
                        word = word,
                        quick5 = quick5,
                        quick3 = quick3,
                        q5complex = quick5.length,
                        q3complex = quick3.length,
                        q5code = quick5.serialCode,
                        q3code = quick3.serialCode,
                )
        }

        private fun quickCode(cangjie: String): String {
                return if (cangjie.length > 2) "${cangjie.first()}${cangjie.last()}" else cangjie
        }
}
