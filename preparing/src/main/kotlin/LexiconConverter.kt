package org.jyutping.preparing

import java.io.InputStream

object LexiconConverter {
        fun jyutping(): List<LexiconEntry> {
                val inputStream: InputStream? = object {}.javaClass.classLoader.getResourceAsStream("jyutping.txt")
                if (inputStream == null) error("Can not load jyutping.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                return sourceLines.mapNotNull { convert(it) }.distinct()
        }
        fun pinyin(): List<LexiconEntry> {
                val inputStream: InputStream? = object {}.javaClass.classLoader.getResourceAsStream("pinyin.txt")
                if (inputStream == null) error("Can not load pinyin.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                return sourceLines.mapNotNull { convert(it) }.distinct()
        }
        fun structure(): List<LexiconEntry> {
                val inputStream: InputStream? = object {}.javaClass.classLoader.getResourceAsStream("structure.txt")
                if (inputStream == null) error("Can not load structure.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                return sourceLines.mapNotNull { convert(it) }.distinct()
        }
        private fun convert(text: String): LexiconEntry? {
                val badLineFormat = "bad line format: $text"
                val parts = text.trim().split(PresetString.TAB).map { it.trim() }
                if (parts.count() != 2) error(badLineFormat)
                val word = parts[0]
                val romanization = parts[1]
                val anchors = romanization.split(PresetString.SPACE).mapNotNull { it.firstOrNull() }
                if (anchors.isEmpty()) error(badLineFormat)
                val anchorText = anchors.joinToString(separator = PresetString.EMPTY)
                val anchorCode = anchorText.charCode
                val tenKeyAnchorCode = anchorText.tenKeyCharCode
                if (anchorCode == null) error(badLineFormat)
                if (tenKeyAnchorCode == null) error(badLineFormat)
                val syllableText = romanization.filter { it in 'a'..'z' }
                if (syllableText.isEmpty()) error(badLineFormat)
                val pingCode: Long = syllableText.hashCode().toLong()
                val tenKeyCode: Long = syllableText.tenKeyCharCode ?: 0
                return LexiconEntry(word = word, romanization = romanization, anchors = anchorCode, ping = pingCode, tenKeyAnchors = tenKeyAnchorCode, tenKeyCode = tenKeyCode)
        }
}
