package org.jyutping.jyutping.extensions

import org.jyutping.jyutping.presets.PresetCharacter

fun Char.isSeparatorChar(): Boolean = (this == PresetCharacter.SEPARATOR)
fun Char.isCantoneseTone(): Boolean = PresetCharacter.cantoneseTones.contains(this)
fun Char.isSeparatorOrTone(): Boolean = (this.isSeparatorChar() || this.isCantoneseTone())

fun Char.isReverseLookupTrigger(): Boolean = PresetCharacter.reverseLookupTriggers.contains(this)

/** is CJKV character Unicode code point */
val Int.isIdeographicCodePoint: Boolean
        get() = when (this) {
                0x3007 -> true
                in 0x4E00..0x9FFF -> true
                in 0x3400..0x4DBF -> true
                in 0x20000..0x2A6DF -> true
                in 0x2A700..0x2B73F -> true
                in 0x2B740..0x2B81F -> true
                in 0x2B820..0x2CEAF -> true
                in 0x2CEB0..0x2EBEF -> true
                in 0x30000..0x3134F -> true
                in 0x31350..0x323AF -> true
                in 0x2EBF0..0x2EE5F -> true
                in 0x323B0..0x33479 -> true
                else -> false
        }

/*
U+3007: 〇
U+4E00-U+9FFF: CJK Unified Ideographs
U+3400-U+4DBF: CJK Unified Ideographs Extension A
U+20000-U+2A6DF: CJK Unified Ideographs Extension B
U+2A700-U+2B73F: CJK Unified Ideographs Extension C
U+2B740-U+2B81F: CJK Unified Ideographs Extension D
U+2B820-U+2CEAF: CJK Unified Ideographs Extension E
U+2CEB0-U+2EBEF: CJK Unified Ideographs Extension F
U+30000-U+3134F: CJK Unified Ideographs Extension G
U+31350-U+323AF: CJK Unified Ideographs Extension H
U+2EBF0-U+2EE5F: CJK Unified Ideographs Extension I
U+323B0-U+33479: CJK Unified Ideographs Extension J
*/
