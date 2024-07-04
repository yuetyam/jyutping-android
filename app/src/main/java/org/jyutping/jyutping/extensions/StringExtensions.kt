package org.jyutping.jyutping.extensions

import android.icu.text.Transliterator

fun String.convertedS2T(): String = Transliterator.getInstance("Simplified-Traditional").transliterate(this)
fun String.convertedT2S(): String = Transliterator.getInstance("Traditional-Simplified").transliterate(this)

val String.Companion.empty: String
        get() = ""

val String.Companion.space: String
        get() = " "

val String.Companion.separator: String
        get() = "'"
