package org.jyutping.preparing

import java.io.InputStream

data class TextMarkLexicon(
        val input: String,
        val mark: String,
        val pingCode: Int,
        val charCode: Long,
        val tenKeyCode: Long
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is TextMarkLexicon) return false
                return this.input == other.input && this.mark == other.mark
        }
        override fun hashCode(): Int {
                return input.hashCode() * 31 + mark.hashCode()
        }
        companion object {
                fun generate(): List<TextMarkLexicon> {
                        val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("mark.txt") ?: error("Can not load mark.txt")
                        val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() && line.startsWith("#").not() } }
                        return sourceLines.map { it.trim() }.distinct().map { convert(it) }.distinct()
                }
                private fun convert(text: String): TextMarkLexicon {
                        val parts = text.trim().split("\t").map { it.trim() }
                        if (parts.count() != 2) error("bad line format: $text")
                        val input = parts[0]
                        val mark = parts[1]
                        val pingCode = input.hashCode()
                        val charCode = input.charCode ?: 0
                        val tenKeyCode = input.tenKeyCharCode ?: 0
                        return TextMarkLexicon(input = input, mark = mark, pingCode = pingCode, charCode = charCode, tenKeyCode = tenKeyCode)
                }
        }
}
