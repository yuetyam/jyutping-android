package org.jyutping.jyutping.extensions

import android.icu.text.Transliterator
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
 * Format text with separators and tones
 * @return Formatted text for mark
 */
fun String.formattedForMark(): String {
        val blocks = this.map { if (it.isSeparatorOrTone()) "$it " else it.toString() }
        return blocks.joinToString(separator = PresetString.EMPTY).trimEnd()
}

/**
 * Create Emoji text from the given code point String.
 * @param codepoint Unicode code point. Example: 1F469.200D.1F373
 * @return Emoji / Symbol character(s)
 */
fun generateSymbol(codepoint: String) = codepoint
        .split(".")
        .map { it.toInt(radix = 16) }
        .flatMap { Character.toChars(it).toList() }
        .joinToString(PresetString.EMPTY)
