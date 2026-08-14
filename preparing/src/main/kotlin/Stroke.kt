package org.jyutping.preparing

data class StrokeEntry(
        val word: String,
        val stroke: String,
        val complex: Int,
        val code: Long,
)

object Stroke {
        private val codeMap: Map<Char, Int> = mapOf(
                'w' to 1,
                's' to 2,
                'a' to 3,
                'd' to 4,
                'z' to 5,
                'h' to 1,
                'p' to 3,
                'n' to 4,
                '1' to 1,
                '2' to 2,
                '3' to 3,
                '4' to 4,
                '5' to 5,
        )

        fun generate(): List<StrokeEntry> {
                val characters = LexiconConverter.jyutpingSourceLines.mapNotNull { line ->
                        val word = line.substringBefore(PresetString.TAB).trim()
                        word.takeIf { it.characterCount() == 1 }
                }.distinct()
                return characters.flatMap { word ->
                        strokeMap[word].orEmpty().mapNotNull { text ->
                                val codes = text.mapNotNull(codeMap::get)
                                require(codes.size == text.length) { "bad stroke format: $word = $text" }
                                if (codes.size > 30) return@mapNotNull null
                                StrokeEntry(
                                        word = word,
                                        stroke = codes.joinToString(PresetString.EMPTY),
                                        complex = codes.size,
                                        code = codes.decimalOverflowed(),
                                )
                        }
                }.distinct()
        }

        private val strokeMap: Map<String, List<String>> by lazy {
                readResourceLines("stroke.txt").fold(mutableMapOf<String, MutableList<String>>()) { result, line ->
                        val parts = line.split(PresetString.TAB)
                        if (parts.size == 2) {
                                result.getOrPut(parts[0]) { mutableListOf() }.add(parts[1])
                        }
                        result
                }
        }
}
