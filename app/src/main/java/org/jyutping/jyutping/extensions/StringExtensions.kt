package org.jyutping.jyutping.extensions

import android.icu.text.Transliterator

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
