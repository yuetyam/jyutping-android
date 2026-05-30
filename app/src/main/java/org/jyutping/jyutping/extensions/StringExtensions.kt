package org.jyutping.jyutping.extensions

import android.icu.text.Transliterator
import org.jyutping.jyutping.presets.PresetCharacter
import org.jyutping.jyutping.presets.PresetString

fun String.convertedS2T(): String = Transliterator.getInstance("Simplified-Traditional").transliterate(this)
// fun String.convertedT2S(): String = Transliterator.getInstance("Traditional-Simplified").transliterate(this)

/**
 * Convert v/x/q to Cantonese tone digits
 * @return Converted text with digit tones
 */
fun String.toneConverted(): String = buildString(length) {
        val textLength = this@toneConverted.length
        var index = 0
        while (index < textLength) {
                val char = this@toneConverted[index]
                val isPaired = (index + 1 < textLength) && (char == this@toneConverted[index + 1])
                if (isPaired) {
                        val replacement = when (char) {
                                ToneCharSet.LETTER_V -> ToneCharSet.DIGIT4
                                ToneCharSet.LETTER_X -> ToneCharSet.DIGIT5
                                ToneCharSet.LETTER_Q -> ToneCharSet.DIGIT6
                                else -> null
                        }
                        if (replacement != null) {
                                append(replacement)
                                index += 2
                                continue
                        }
                }
                when (char) {
                        ToneCharSet.LETTER_V -> append(ToneCharSet.DIGIT1)
                        ToneCharSet.LETTER_X -> append(ToneCharSet.DIGIT2)
                        ToneCharSet.LETTER_Q -> append(ToneCharSet.DIGIT3)
                        else -> append(char)
                }
                index += 1
        }
}
private object ToneCharSet {
        const val LETTER_V: Char = 'v'
        const val LETTER_X: Char = 'x'
        const val LETTER_Q: Char = 'q'
        const val DIGIT1  : Char = '1'
        const val DIGIT2  : Char = '2'
        const val DIGIT3  : Char = '3'
        const val DIGIT4  : Char = '4'
        const val DIGIT5  : Char = '5'
        const val DIGIT6  : Char = '6'
}

/**
 * Inserts a space after any non-letter character
 * @return Formatted text for preview-mark
*/
fun String.markFormatted(): String {
        var result: String = PresetString.EMPTY
        for (index in indices) {
                val character = this[index]
                result += character
                if (character.isBasicLatinLetter.negative && index < lastIndex) {
                        result += PresetCharacter.SPACE
                }
        }
        return result
}

/**
 * A space-separated list of Unicode code points in `U+XXXX` notation.
 *
 * Each code point is formatted as uppercase hexadecimal, padded to at least four digits.
 *
 * For example, the character é in decomposed form (e + combining acute accent) is represented as: "U+0065 U+0301".
 */
val String.codePointsText: String
        get() = codePoints().toArray().joinToString(separator = PresetString.SPACE) { String.format("U+%04X", it) }

/**
 * Convert Emoji text to Unicode code points as a String
 * @return Formatted Unicode code point text. Example: 1F469.200D.1F373
 */
fun String.formattedCodePointsText(): String = codePoints().toArray().joinToString(separator = PresetString.PERIOD) { it.toString(radix = 16).uppercase() }

/**
 * Create Emoji/Symbol text from this code point text.
 * Example: 1F469.200D.1F373
 * @return Emoji / Symbol text
 */
fun String.generateSymbol(): String {
        val text = this
        return buildString {
                text.split(".").forEach { appendCodePoint(it.toInt(radix = 16)) }
        }
}

/**
 * Convert Unicode code point text to Character.
 */
fun String.toCharText(): String {
        val text = this
        return buildString {
                appendCodePoint(text.toInt(radix = 16))
        }
}

/**
 * Count of characters.
 *
 * A non-BMP character would count as one.
 * */
val String.characterCount: Int
        get() = codePoints().count().toInt()
