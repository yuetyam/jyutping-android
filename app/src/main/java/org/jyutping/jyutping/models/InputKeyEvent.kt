package org.jyutping.jyutping.models

import org.jyutping.jyutping.extensions.virtualInputCode

typealias VirtualInputKey = InputKeyEvent

/**
 * Basic input key event
 * @property text Key text
 * @property code Unique identifier code
 * @property keyCode Android KeyEvent code value
 */
data class InputKeyEvent(
        val text: String,
        val code: Int,
        val keyCode: Int
) : Comparable<InputKeyEvent> {

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is InputKeyEvent) return false
                return this.code == other.code
        }
        override fun hashCode(): Int {
                return code.hashCode()
        }
        override fun compareTo(other: InputKeyEvent): Int {
                return this.code.compareTo(other.code)
        }

        /** Digits 0-9 */
        val isNumber: Boolean
                get() = this in InputKeyEvent.digitSet

        /** Cantonese tone digits 1-6 */
        val isToneNumber: Boolean
                get() = this in InputKeyEvent.toneSet

        /** Letters a-z */
        val isLetter: Boolean
                get() = this in InputKeyEvent.alphabetSet

        /** Letters a-z excluded tone letters v x q */
        val isSyllableLetter: Boolean
                get() = isLetter && isToneLetter.not()

        /** v, x, q */
        val isToneLetter: Boolean
                get() = when (this) {
                        letterV, letterX, letterQ -> true
                        else -> false
                }

        /** v, x, q and 1-6 */
        val isToneEvent: Boolean
                get() = isToneLetter || isToneNumber

        /** r, v, x, q */
        val isReverseLookupTrigger: Boolean
                get() = when (this) {
                        letterR, letterV, letterX, letterQ -> true
                        else -> false
                }

        /** Separator; Delimiter; Quote */
        val isApostrophe: Boolean
                get() = this == InputKeyEvent.apostrophe

        /** Grave accent; Backtick; Backquote */
        val isGrave: Boolean
                get() = this == InputKeyEvent.grave

        /** Integer number of the number key */
        val digit: Int?
                get() = if (this.isNumber) (this.code - 10) else null

        companion object {
                val number0 = InputKeyEvent("0", 10, 7)
                val number1 = InputKeyEvent("1", 11, 8)
                val number2 = InputKeyEvent("2", 12, 9)
                val number3 = InputKeyEvent("3", 13, 10)
                val number4 = InputKeyEvent("4", 14, 11)
                val number5 = InputKeyEvent("5", 15, 12)
                val number6 = InputKeyEvent("6", 16, 13)
                val number7 = InputKeyEvent("7", 17, 14)
                val number8 = InputKeyEvent("8", 18, 15)
                val number9 = InputKeyEvent("9", 19, 16)

                val letterA = InputKeyEvent("a", 20, 29)
                val letterB = InputKeyEvent("b", 21, 30)
                val letterC = InputKeyEvent("c", 22, 31)
                val letterD = InputKeyEvent("d", 23, 32)
                val letterE = InputKeyEvent("e", 24, 33)
                val letterF = InputKeyEvent("f", 25, 34)
                val letterG = InputKeyEvent("g", 26, 35)
                val letterH = InputKeyEvent("h", 27, 36)
                val letterI = InputKeyEvent("i", 28, 37)
                val letterJ = InputKeyEvent("j", 29, 38)
                val letterK = InputKeyEvent("k", 30, 39)
                val letterL = InputKeyEvent("l", 31, 40)
                val letterM = InputKeyEvent("m", 32, 41)
                val letterN = InputKeyEvent("n", 33, 42)
                val letterO = InputKeyEvent("o", 34, 43)
                val letterP = InputKeyEvent("p", 35, 44)
                val letterQ = InputKeyEvent("q", 36, 45)
                val letterR = InputKeyEvent("r", 37, 46)
                val letterS = InputKeyEvent("s", 38, 47)
                val letterT = InputKeyEvent("t", 39, 48)
                val letterU = InputKeyEvent("u", 40, 49)
                val letterV = InputKeyEvent("v", 41, 50)
                val letterW = InputKeyEvent("w", 42, 51)
                val letterX = InputKeyEvent("x", 43, 52)
                val letterY = InputKeyEvent("y", 44, 53)
                val letterZ = InputKeyEvent("z", 45, 54)

                /** Separator; Delimiter; Quote */
                val apostrophe = InputKeyEvent("'", 47, 75)

                /** Grave accent; Backtick; Backquote */
                val grave = InputKeyEvent("`", 48, 68)

                // Digit numbers [0-9]
                val digitSet: Set<InputKeyEvent> = setOf(
                        number0, number1, number2, number3, number4,
                        number5, number6, number7, number8, number9
                )

                // Cantonese tone digits [1-6]
                val toneSet: Set<InputKeyEvent> = setOf(
                        number1, number2, number3, number4, number5, number6
                )

                // Letters [a-z]
                val alphabetSet: Set<InputKeyEvent> = setOf(
                        letterA, letterB, letterC, letterD, letterE, letterF, letterG,
                        letterH, letterI, letterJ, letterK, letterL, letterM, letterN,
                        letterO, letterP, letterQ, letterR, letterS, letterT, letterU,
                        letterV, letterW, letterX, letterY, letterZ
                )

                fun matchVirtualInputKey(code: Int): InputKeyEvent? {
                        return alphabetSet.firstOrNull { it.code == code } ?: digitSet.firstOrNull { it.code == code }
                }
                fun matchVirtualInputKey(char: Char): InputKeyEvent? {
                        val code = char.virtualInputCode ?: return null
                        return alphabetSet.firstOrNull { it.code == code } ?: digitSet.firstOrNull { it.code == code }
                }
        }
}

val Long.matchedVirtualInputKeys: List<InputKeyEvent>
        get() {
                var number = this
                val codes = mutableListOf<Long>()
                while (number > 0) {
                        codes.add(number % 100L)
                        number /= 100L
                }
                return codes.reversed().mapNotNull { VirtualInputKey.matchVirtualInputKey(code = it.toInt()) }
        }

fun List<VirtualInputKey>.combinedCode(): Long {
        if (this.size > 9) return 0L
        return this.fold(0L) { acc, event -> acc * 100L + event.code.toLong() }
}

fun List<VirtualInputKey>.anchorsCode(): Long {
        return this.map { if (it == InputKeyEvent.letterY) VirtualInputKey.letterJ else it }.combinedCode()
}
