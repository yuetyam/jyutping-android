package org.jyutping.jyutping.extensions

import org.jyutping.jyutping.presets.PresetCharacter

/** a-z */
val Char.isLowercaseBasicLatinLetter: Boolean
        get() = (this in 'a'..'z')

/** A-Z */
val Char.isUppercaseBasicLatinLetter: Boolean
        get() = (this in 'A'..'Z')

/** a-z || A-Z */
val Char.isBasicLatinLetter: Boolean
        get() = (this in 'a'..'z') || (this in 'A'..'Z')

/** 0-9 */
val Char.isBasicDigit: Boolean
        get() = (this in '0'..'9')

/** 1-6 */
val Char.isCantoneseToneDigit: Boolean
        get() = (this in '1'..'6')

/** Is not BasicLatinLetter */
val Char.isNotLetter: Boolean
        get() = this.isBasicLatinLetter.negative

/** U+0020 */
val Char.isSpace: Boolean
        get() = (this == PresetCharacter.SPACE)

/** U+0027. Separator; Delimiter; Quote */
val Char.isApostrophe: Boolean
        get() = (this == PresetCharacter.APOSTROPHE)

/** r, v, x, q */
val Char.isReverseLookupTrigger: Boolean
        get() = PresetCharacter.reverseLookupTriggers.contains(this)


/** ideographic or supplemental CJKV code point */
val Int.isGenericCJKVCodePoint: Boolean
        get() = (this.isIdeographicCodePoint || this.isSupplementalCJKVCodePoint)

/** CJKV character Unicode code point */
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

/** CJKV supplemental code point */
val Int.isSupplementalCJKVCodePoint: Boolean
        get() = when (this) {
                in 0x2E80..0x2E99 -> true
                in 0x2E9B..0x2EF3 -> true
                in 0x2F00..0x2FD5 -> true
                in 0xF900..0xFA6D -> true
                in 0xFA70..0xFAD9 -> true
                in 0x2F800..0x2FA1D -> true
                else -> false
        }

/*
U+3007: Character Zero
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

/*
U+2E80-U+2E99: CJK Radicals Supplement
U+2E9B-U+2EF3: CJK Radicals Supplement
U+2F00-U+2FD5: Kangxi Radicals
U+F900-U+FA6D: CJK Compatibility Ideographs
U+FA70-U+FAD9: CJK Compatibility Ideographs
U+2F800-U+2FA1D: CJK Compatibility Ideographs Supplement
*/

