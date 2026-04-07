package org.jyutping.jyutping.ninekey

/**
 * 10-key / T9 keyboard element
 * @param number Key number; identified code
 * */
enum class Combo(val number: Int) {

        Special(1),
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
                private val textMap: Map<Combo, String> = mapOf(
                        Special to "R",
                        ABC  to "ABC",
                        DEF  to "DEF",
                        GHI  to "GHI",
                        JKL  to "JKL",
                        MNO  to "MNO",
                        PQRS to "PQRS",
                        TUV  to "TUV",
                        WXYZ to "WXYZ"
                )
                private val letterMap: Map<Combo, List<String>> = mapOf(
                        Special to listOf("r"),
                        ABC  to listOf("a", "b", "c"),
                        DEF  to listOf("d", "e", "f"),
                        GHI  to listOf("g", "h", "i"),
                        JKL  to listOf("j", "k", "l"),
                        MNO  to listOf("m", "n", "o"),
                        PQRS to listOf("p", "s"),
                        TUV  to listOf("t", "u"),
                        WXYZ to listOf("w", "y", "z")
                )
        }
}
