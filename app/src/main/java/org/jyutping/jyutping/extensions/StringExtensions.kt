package org.jyutping.jyutping.extensions

import android.icu.text.Transliterator
import org.jyutping.jyutping.presets.PresetCharacter
import org.jyutping.jyutping.presets.PresetString

fun String.convertedS2T(): String = Transliterator.getInstance("Simplified-Traditional").transliterate(this)
fun String.convertedT2S(): String = Transliterator.getInstance("Traditional-Simplified").transliterate(this)

/**
 * Convert v/x/q to the tone digits
 * @return Converted text with digital tones
 */
fun String.toneConverted(): String = this
        .replace("vv", "4")
        .replace("xx", "5")
        .replace("qq", "6")
        .replace('v', '1')
        .replace('x', '2')
        .replace('q', '3')

/**
 * Inserts a space after any non-letter character
 * @return Formatted text for mark
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
 * Example: U+5929
 */
fun String.codePointText() = this.codePoints().toArray().joinToString(separator = PresetString.SPACE) { "U+" + it.toString(radix = 16).uppercase() }

/**
 * Convert Emoji text to Unicode code point text
 * @return Formatted Unicode code point text. Example: 1F469.200D.1F373
 */
fun String.formattedCodePointText(): String = this.codePoints().toArray().joinToString(separator = ".") { it.toString(radix = 16).uppercase() }

/**
 * Create Emoji/Symbol text from this code point text.
 * Example: 1F469.200D.1F373
 * @return Emoji / Symbol text
 */
fun String.generateSymbol(): String {
        val text = this
        return buildString {
                text.split(".").map { it.toInt(radix = 16) }.forEach { appendCodePoint(it) }
        }
}

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
        get() = this.codePoints().count().toInt()
