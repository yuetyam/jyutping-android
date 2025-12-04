package org.jyutping.preparing

import java.io.InputStream
import kotlin.io.bufferedReader

data class VariantMap(val left: Int, val right: Int): Comparable<VariantMap> {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is VariantMap) return false
                return this.left == other.left && this.right == other.right
        }
        override fun hashCode(): Int = this.left.hashCode() * 31 + this.right.hashCode()
        override fun compareTo(other: VariantMap): Int = this.left.compareTo(other.left).takeIf { it != 0 } ?: this.right.compareTo(other.right)
}

object CharacterVariant {
        fun process(sourceFileName: String): List<VariantMap> {
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream(sourceFileName) ?: error("Can not load $sourceFileName")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() && line.startsWith("#").not() } }.distinct()
                return sourceLines.flatMap { convert(it) }.distinct().sorted()
        }
        private fun convert(line: String): List<VariantMap> {
                val errorMessage = "bad line format: $line"
                val parts = line.trim().split("\t").map { it.trim() }
                if (parts.count() < 2) error(errorMessage)
                val leftText = parts[0]
                val rightText = parts[1]
                if (leftText.characterCount() != 1) error(errorMessage)
                when (rightText.characterCount()) {
                        0 -> error(errorMessage)
                        1 -> {
                                val leftCharCodePoint = leftText.codePointAt(0)
                                val rightCharCodePoint = rightText.codePointAt(0)
                                if (leftCharCodePoint.isIdeographicCodePoint.negative) {
                                        return emptyList()
                                }
                                if (rightCharCodePoint.isIdeographicCodePoint.negative) {
                                        return emptyList()
                                }
                                if (leftCharCodePoint == rightCharCodePoint) {
                                        return emptyList()
                                }
                                return listOf(VariantMap(left = leftCharCodePoint, right = rightCharCodePoint))
                        }
                        else -> {
                                val leftCharCodePoint = leftText.codePointAt(0)
                                if (leftCharCodePoint.isIdeographicCodePoint.negative) {
                                        return emptyList()
                                }
                                val rightComponents = rightText.split(" ").map { it.trim() }
                                return rightComponents.mapNotNull { text ->
                                        val codePoint = text.codePointAt(0)
                                        if (codePoint.isIdeographicCodePoint) {
                                                VariantMap(left = leftCharCodePoint, right = codePoint)
                                        } else {
                                                null
                                        }
                                }
                        }
                }
        }
}
