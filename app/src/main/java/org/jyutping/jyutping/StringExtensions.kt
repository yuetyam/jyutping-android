package org.jyutping.jyutping

import android.icu.text.Transliterator

fun String.convertedS2T(): String {
        val transliterator = Transliterator.getInstance("Simplified-Traditional")
        return transliterator.transliterate(this)
}
