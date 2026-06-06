package org.jyutping.preparing

import java.io.InputStream

data class VariantMap(val left: Int, val right: Int): Comparable<VariantMap> {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is VariantMap) return false
                return left == other.left
                // return left == other.left && right == other.right
        }
        override fun hashCode(): Int = left.hashCode()
        // override fun hashCode(): Int = left.hashCode() * 31 + right.hashCode()
        override fun compareTo(other: VariantMap): Int = left.compareTo(other.left)
}

object CharacterVariant {
        fun process(sourceFileName: String): List<VariantMap> {
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream(sourceFileName) ?: error("Can not load $sourceFileName")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() && line.startsWith("#").not() } }.distinct()
                return sourceLines.mapNotNull { transform(it) }.distinct().sortedBy { it.left }
        }
        private fun transform(line: String): VariantMap? {
                val errorMessage by lazy { "bad line format: $line" }
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
                                if (leftCharCodePoint.isGenericCJKVCodePoint.negative || rightCharCodePoint.isGenericCJKVCodePoint.negative) return null
                                if (leftCharCodePoint == rightCharCodePoint) return null
                                return VariantMap(left = leftCharCodePoint, right = rightCharCodePoint)
                        }
                        else -> {
                                val leftCharCodePoint = leftText.codePointAt(0)
                                if (leftCharCodePoint.isGenericCJKVCodePoint.negative) return null
                                val rightComponents = rightText.split(" ").map { it.trim() }
                                val rightCharCodePoint = rightComponents.firstOrNull()?.codePointAt(0) ?: return null
                                if (rightCharCodePoint.isGenericCJKVCodePoint.negative) return null
                                if (leftCharCodePoint == rightCharCodePoint) return null
                                return VariantMap(left = leftCharCodePoint, right = rightCharCodePoint)
                        }
                }
        }

        @Deprecated(message = "Use transform() instead", level = DeprecationLevel.ERROR)
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
                                if (leftCharCodePoint.isGenericCJKVCodePoint.negative) {
                                        return emptyList()
                                }
                                if (rightCharCodePoint.isGenericCJKVCodePoint.negative) {
                                        return emptyList()
                                }
                                if (leftCharCodePoint == rightCharCodePoint) {
                                        return emptyList()
                                }
                                return listOf(VariantMap(left = leftCharCodePoint, right = rightCharCodePoint))
                        }
                        else -> {
                                val leftCharCodePoint = leftText.codePointAt(0)
                                if (leftCharCodePoint.isGenericCJKVCodePoint.negative) {
                                        return emptyList()
                                }
                                val rightComponents = rightText.split(" ").map { it.trim() }
                                return rightComponents.mapNotNull { text ->
                                        val codePoint = text.codePointAt(0)
                                        if (codePoint.isGenericCJKVCodePoint) {
                                                VariantMap(left = leftCharCodePoint, right = codePoint)
                                        } else {
                                                null
                                        }
                                }
                        }
                }
        }
}
