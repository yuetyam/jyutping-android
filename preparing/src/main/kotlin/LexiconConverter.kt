package org.jyutping.preparing

import java.io.InputStream

object LexiconConverter {
        fun jyutping(): List<LexiconEntry> {
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("jyutping.txt") ?: error("Can not load jyutping.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                return sourceLines.map { convert(it) }.distinct()
        }
        fun pinyin(): List<LexiconEntry> {
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("pinyin.txt") ?: error("Can not load pinyin.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                return sourceLines.map { convert(it) }.distinct()
        }
        fun structure(): List<LexiconEntry> {
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("structure.txt") ?: error("Can not load structure.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                return sourceLines.map { convert(it) }.distinct()
        }
        private fun convert(text: String): LexiconEntry {
                val badLineFormat = "bad line format: $text"
                val parts = text.trim().split(PresetString.TAB).map { it.trim() }
                if (parts.count() != 2) error(badLineFormat)
                val word = parts[0]
                val romanization = parts[1]
                val anchors = romanization.split(PresetString.SPACE).mapNotNull { it.firstOrNull() }
                if (anchors.isEmpty()) error(badLineFormat)
                val anchorText = anchors.joinToString(separator = PresetString.EMPTY)
                val anchorCode = anchorText.charCode
                val nineKeyAnchorCode = anchorText.nineKeyCharCode
                if (anchorCode == null) error(badLineFormat)
                if (nineKeyAnchorCode == null) error(badLineFormat)
                val syllableText = romanization.filter { it in 'a'..'z' }
                if (syllableText.isEmpty()) error(badLineFormat)
                val spell: Long = syllableText.hashCode().toLong()
                val nineKeyCode: Long = syllableText.nineKeyCharCode ?: 0
                return LexiconEntry(word = word, romanization = romanization, anchors = anchorCode, spell = spell, nineKeyAnchors = nineKeyAnchorCode, nineKeyCode = nineKeyCode)
        }
}
