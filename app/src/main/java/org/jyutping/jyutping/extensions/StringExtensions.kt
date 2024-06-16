package org.jyutping.jyutping.extensions

import android.icu.text.Transliterator

fun String.convertedS2T(): String = Transliterator.getInstance("Simplified-Traditional").transliterate(this)
