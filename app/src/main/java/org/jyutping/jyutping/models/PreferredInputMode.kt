package org.jyutping.jyutping.models

enum class PreferredInputMode(val identifier: Int) {

        // Auto(0),
        Cantonese(1),
        ABC(2),
        Previous(3);

        val isCantonese: Boolean
                get() = (this == Cantonese)

        val isABC: Boolean
                get() = (this == ABC)

        val isPrevious: Boolean
                get() = (this == Previous)

        companion object {
                fun modeOf(value: Int): PreferredInputMode = entries.find { it.identifier == value } ?: Cantonese
        }
}
