package org.jyutping.jyutping.models

import org.jyutping.jyutping.stroke.StrokeVirtualKey

/**
 * Internal virtual input key
 * @property character Key character
 * @property text Key character as String
 * @property code Unique identifier code
 * @property keyCode Android KeyEvent code value
 */
data class VirtualInputKey(
        val character: Char,
        val text: String = character.toString(),
        val code: Int,
        val keyCode: Int
) : Comparable<VirtualInputKey> {

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is VirtualInputKey) return false
                return this.code == other.code
        }
        override fun hashCode(): Int {
                return code.hashCode()
        }
        override fun compareTo(other: VirtualInputKey): Int {
                return this.code.compareTo(other.code)
        }

        /** Digits 0-9 */
        val isNumber: Boolean
                get() = (this in digitSet)

        /** Cantonese tone digits 1-6 */
        val isToneNumber: Boolean
                get() = (this in toneSet)

        /** Letters a-z */
        val isLetter: Boolean
                get() = (this in alphabetSet)

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
        val isToneInputKey: Boolean
                get() = isToneLetter || isToneNumber

        /** r, v, x, q */
        val isReverseLookupTrigger: Boolean
                get() = when (this) {
                        letterR, letterV, letterX, letterQ -> true
                        else -> false
                }

        /** Separator; Delimiter; Quote */
        val isApostrophe: Boolean
                get() = (this == apostrophe)

        /** Grave accent; Backtick; Backquote */
        val isGrave: Boolean
                get() = (this == grave)

        /** Integer number of the number key */
        val digit: Int?
                get() = if (this.isNumber) (this.code - 10) else null

        val strokeVirtualKey: StrokeVirtualKey?
                get() = StrokeVirtualKey.strokeKeyOf(this)

        val displayStrokeKeyText: String?
                get() = StrokeVirtualKey.displayStrokeKeyTextOf(this)

        companion object {
                val number0 = VirtualInputKey('0', code = 10, keyCode = 7)
                val number1 = VirtualInputKey('1', code = 11, keyCode = 8)
                val number2 = VirtualInputKey('2', code = 12, keyCode = 9)
                val number3 = VirtualInputKey('3', code = 13, keyCode = 10)
                val number4 = VirtualInputKey('4', code = 14, keyCode = 11)
                val number5 = VirtualInputKey('5', code = 15, keyCode = 12)
                val number6 = VirtualInputKey('6', code = 16, keyCode = 13)
                val number7 = VirtualInputKey('7', code = 17, keyCode = 14)
                val number8 = VirtualInputKey('8', code = 18, keyCode = 15)
                val number9 = VirtualInputKey('9', code = 19, keyCode = 16)

                val letterA = VirtualInputKey('a', code = 20, keyCode = 29)
                val letterB = VirtualInputKey('b', code = 21, keyCode = 30)
                val letterC = VirtualInputKey('c', code = 22, keyCode = 31)
                val letterD = VirtualInputKey('d', code = 23, keyCode = 32)
                val letterE = VirtualInputKey('e', code = 24, keyCode = 33)
                val letterF = VirtualInputKey('f', code = 25, keyCode = 34)
                val letterG = VirtualInputKey('g', code = 26, keyCode = 35)
                val letterH = VirtualInputKey('h', code = 27, keyCode = 36)
                val letterI = VirtualInputKey('i', code = 28, keyCode = 37)
                val letterJ = VirtualInputKey('j', code = 29, keyCode = 38)
                val letterK = VirtualInputKey('k', code = 30, keyCode = 39)
                val letterL = VirtualInputKey('l', code = 31, keyCode = 40)
                val letterM = VirtualInputKey('m', code = 32, keyCode = 41)
                val letterN = VirtualInputKey('n', code = 33, keyCode = 42)
                val letterO = VirtualInputKey('o', code = 34, keyCode = 43)
                val letterP = VirtualInputKey('p', code = 35, keyCode = 44)
                val letterQ = VirtualInputKey('q', code = 36, keyCode = 45)
                val letterR = VirtualInputKey('r', code = 37, keyCode = 46)
                val letterS = VirtualInputKey('s', code = 38, keyCode = 47)
                val letterT = VirtualInputKey('t', code = 39, keyCode = 48)
                val letterU = VirtualInputKey('u', code = 40, keyCode = 49)
                val letterV = VirtualInputKey('v', code = 41, keyCode = 50)
                val letterW = VirtualInputKey('w', code = 42, keyCode = 51)
                val letterX = VirtualInputKey('x', code = 43, keyCode = 52)
                val letterY = VirtualInputKey('y', code = 44, keyCode = 53)
                val letterZ = VirtualInputKey('z', code = 45, keyCode = 54)

                /** Separator; Delimiter; Quote */
                val apostrophe = VirtualInputKey('\'', code = 47, keyCode = 75)

                /** Grave accent; Backtick; Backquote */
                val grave = VirtualInputKey('`', code = 48, keyCode = 68)

                // Digit numbers [0-9]
                val digitSet: Set<VirtualInputKey> = setOf(
                        number0, number1, number2, number3, number4,
                        number5, number6, number7, number8, number9
                )

                // Cantonese tone digits [1-6]
                val toneSet: Set<VirtualInputKey> = setOf(
                        number1, number2, number3, number4, number5, number6
                )

                // Letters [a-z]
                val alphabetSet: Set<VirtualInputKey> = setOf(
                        letterA, letterB, letterC, letterD, letterE, letterF, letterG,
                        letterH, letterI, letterJ, letterK, letterL, letterM, letterN,
                        letterO, letterP, letterQ, letterR, letterS, letterT, letterU,
                        letterV, letterW, letterX, letterY, letterZ
                )

                fun matchVirtualInputKey(code: Int): VirtualInputKey? = alphabetSet.find { it.code == code } ?: digitSet.find { it.code == code }
                fun matchVirtualInputKey(char: Char): VirtualInputKey? {
                        val code = char.interCode ?: return null
                        return alphabetSet.find { it.code == code } ?: digitSet.find { it.code == code }
                }
                fun matchVirtualKey(eventCode: Int): VirtualInputKey? = when (eventCode) {
                        grave.keyCode -> grave
                        apostrophe.keyCode -> apostrophe
                        else -> alphabetSet.find { it.keyCode == eventCode } ?: toneSet.find { it.keyCode == eventCode }
                }
        }
}

val Long.matchedVirtualInputKeys: List<VirtualInputKey>
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
        return this.fold(0L) { acc, key -> acc * 100L + key.code.toLong() }
}

fun List<VirtualInputKey>.anchorsCode(): Long {
        return this.map { if (it == VirtualInputKey.letterY) VirtualInputKey.letterJ else it }.combinedCode()
}
