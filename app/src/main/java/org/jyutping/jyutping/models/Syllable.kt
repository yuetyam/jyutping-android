package org.jyutping.jyutping.models

import org.jyutping.jyutping.presets.PresetString

data class Syllable(
        val aliasCode: Long,
        val originCode: Long
) : Comparable<Syllable> {

        val alias: List<InputKeyEvent> = aliasCode.matchedVirtualInputKeys
        val origin: List<InputKeyEvent> = originCode.matchedVirtualInputKeys

        val aliasText: String
                get() = alias.joinToString(PresetString.EMPTY) { it.text }

        val originText: String
                get() = origin.joinToString(PresetString.EMPTY) { it.text }

        override fun compareTo(other: Syllable): Int {
                val aliasQuotient = this.aliasCode / other.aliasCode
                if (aliasQuotient != 0L) {
                        return -1
                } else {
                        val originQuotient = this.originCode / other.originCode
                        return if (originQuotient > 0L) -1 else 1
                }
        }
}
