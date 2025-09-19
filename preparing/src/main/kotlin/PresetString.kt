package org.jyutping.preparing

object PresetString {
        const val EMPTY: String = ""
        const val SPACE: String = " "
        const val TAB: String = "\t"

        /** Quote mark; delimiter; apostrophe */
        const val SEPARATOR: String = "'"
}

fun String.characterCount(): Int = this.codePoints().count().toInt()
