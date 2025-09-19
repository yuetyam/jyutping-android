package org.jyutping.preparing

import java.io.InputStream
import kotlin.io.bufferedReader

object Hant2Hans {
        fun generate(): List<Pair<Int, Int>> {
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("t2s.txt") ?: error("Can not load t2s.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() && line.startsWith("#").not() } }
                return sourceLines.map { it.trim() }.distinct().map { convert(it) }
        }
        private fun convert(text: String): Pair<Int, Int> {
                val parts = text.trim().split("\t").map { it.trim() }
                if (parts.count() != 2) error("bad line format: $text")
                val traditional = parts[0]
                val simplified = parts[1]
                if (traditional == simplified) error("bad line format: $text")
                val traditionalCode = traditional.codePointAt(0)
                val simplifiedCode = simplified.codePointAt(0)
                return Pair(traditionalCode, simplifiedCode)
        }
}
