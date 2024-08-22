package org.jyutping.jyutping.keyboard

/**
 * For Cangjie/Quick/Stroke Reverse Lookup
 * @property text Cantonese word.
 * @property input User input.
 * @property complex Complexity, the count of Cangjie/Quick/Stroke code.
 * @property order Rank. Smaller is preferred.
 */
data class ShapeLexicon(val text: String, val input: String, val complex: Int, val order: Int) {
        override fun equals(other: Any?): Boolean {
                if (other !is ShapeLexicon) return false
                return text == other.text
        }
        override fun hashCode(): Int {
                return text.hashCode()
        }
}
