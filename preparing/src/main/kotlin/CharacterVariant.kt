package org.jyutping.preparing

data class VariantMap(val left: Int, val right: Int) : Comparable<VariantMap> {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is VariantMap) return false
                return left == other.left
        }

        override fun hashCode(): Int = left.hashCode()

        override fun compareTo(other: VariantMap): Int = left.compareTo(other.left)
}

object CharacterVariant {
        fun generate(sourceFileName: String): List<VariantMap> {
                return readResourceLines(sourceFileName)
                        .map(String::trim)
                        .filter { it.isNotEmpty() && !it.startsWith("#") }
                        .distinct()
                        .mapNotNull(::transform)
                        .distinct()
                        .sorted()
        }

        private fun transform(line: String): VariantMap? {
                val errorMessage = "bad line format: $line"
                val parts = line.split(PresetString.TAB).map(String::trim)
                require(parts.size >= 2) { errorMessage }
                val leftText = parts[0]
                val rightText = parts[1]
                require(leftText.characterCount() == 1) { errorMessage }
                require(rightText.isNotEmpty()) { errorMessage }
                val left = leftText.codePointAt(0)
                val isSingleTarget = rightText.characterCount() == 1
                val rightComponent = if (isSingleTarget) rightText else rightText.substringBefore(PresetString.SPACE)
                val right = rightComponent.codePointAt(0)
                if (!left.isGenericCJKVCodePoint || !right.isGenericCJKVCodePoint) {
                        println("Not Generic Ideographic: $line")
                        return null
                }
                if (left == right) {
                        require(!isSingleTarget) { errorMessage }
                        return null
                }
                return VariantMap(left = left, right = right)
        }
}
