package org.jyutping.preparing

object LexiconConverter {
        val jyutpingSourceLines: List<String> by lazy {
                readResourceLines("jyutping.txt")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
        }

        fun jyutping(): List<LexiconEntry> {
                return jyutpingSourceLines.map(::convert)
        }

        fun pinyin(): List<LexiconEntry> {
                return readResourceLines("pinyin.txt")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .map(::convert)
        }

        fun structure(): List<LexiconEntry> {
                val transformedLines = readResourceLines("structure.txt").map { line ->
                        val parts = line.split(PresetString.TAB)
                        require(parts.size == 3) { "bad line format: $line" }
                        parts[0] + PresetString.TAB + parts[2]
                }
                return transformedLines.distinct().map(::convert)
        }

        private fun convert(text: String): LexiconEntry {
                val badLineFormat = "bad line format: $text"
                val parts = text.trim().split(PresetString.TAB).map { it.trim() }
                require(parts.size == 2) { badLineFormat }
                val word = parts[0]
                val romanization = parts[1]
                val phones = romanization.filterNot { it.isBasicDigit }.split(PresetString.SPACE)
                val complexity = phones.map(String::length).decimalOverflowed()
                val anchorText = phones.mapNotNull(String::firstOrNull).joinToString(PresetString.EMPTY)
                val letters = romanization.filter(Char::isLowercaseBasicLatinLetter)
                require(letters.isNotEmpty()) { badLineFormat }
                return LexiconEntry(
                        word = word,
                        romanization = romanization,
                        charCount = word.characterCount(),
                        letterCount = letters.length,
                        complexity = complexity,
                        anchors = anchorText.serialCode,
                        spell = letters.serialCode,
                        nineKeyAnchors = anchorText.keypadCode,
                        nineKeySpell = letters.keypadCode,
                )
        }
}
