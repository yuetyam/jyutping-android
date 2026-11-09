package org.jyutping.jyutping.models

/** 9-key / T9 keyboard element
 * @param digit Keypad number; identifier
 */
enum class KeypadCombo(val digit: Int) {

        ABC(2),
        DEF(3),
        GHI(4),
        JKL(5),
        MNO(6),
        PQRS(7),
        TUV(8),
        WXYZ(9);

        /** Key display text */
        val text: String
                get() = textMap[this] ?: "ABC"

        /** Jyutping syllable compatible letters */
        val letters: List<String>
                get() = letterMap[this] ?: listOf("a")

        companion object {
                private val textMap: Map<KeypadCombo, String> = mapOf(
                        ABC  to "ABC",
                        DEF  to "DEF",
                        GHI  to "GHI",
                        JKL  to "JKL",
                        MNO  to "MNO",
                        PQRS to "PQRS",
                        TUV  to "TUV",
                        WXYZ to "WXYZ"
                )
                private val letterMap: Map<KeypadCombo, List<String>> = mapOf(
                        ABC  to listOf("a", "b", "c"),
                        DEF  to listOf("d", "e", "f"),
                        GHI  to listOf("g", "h", "i"),
                        JKL  to listOf("j", "k", "l"),
                        MNO  to listOf("m", "n", "o"),
                        PQRS to listOf("p", "s"),
                        TUV  to listOf("t", "u"),
                        WXYZ to listOf("w", "y", "z")
                )

                fun elementOf(digit: Int): KeypadCombo? = entries.find { it.digit == digit }
        }
}

/** Combines the decimal digit codes using wrapping arithmetic. */
val List<KeypadCombo>.decimalCombinedCode: Long
        get() = this.fold(0L) { acc, combo -> acc * 10L + combo.digit.toLong() }

/** Decomposes a decimal combined code into the matched combos sequence. */
val Long.matchedCombos: List<KeypadCombo>
        get() {
                var number = this
                val digits = mutableListOf<Int>()
                while (number > 0L) {
                        digits.add((number % 10L).toInt())
                        number /= 10L
                }
                return digits.reversed().mapNotNull { KeypadCombo.elementOf(it) }
        }
