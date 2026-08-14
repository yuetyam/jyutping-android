package org.jyutping.preparing

data class PlainText(
        val input: String,
        val word: String,
        val letterCount: Int,
        val spell: Long,
        val nineKeySpell: Long,
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is PlainText) return false
                return input == other.input && word == other.word
        }

        override fun hashCode(): Int = input.hashCode() * 31 + word.hashCode()

        companion object {
                fun convert(): List<PlainText> {
                        return readResourceLines("text.txt")
                                .map(String::trim)
                                .filter { it.isNotEmpty() && !it.startsWith("#") }
                                .distinct()
                                .map { line ->
                                        val parts = line.split(PresetString.TAB).map(String::trim)
                                        require(parts.size >= 2) { "Bad line format in text.txt: $line" }
                                        val input = parts[0]
                                        PlainText(
                                                input = input,
                                                word = parts[1],
                                                letterCount = input.length,
                                                spell = input.serialCode,
                                                nineKeySpell = input.keypadCode,
                                        )
                                }
                                .distinct()
                }
        }
}
