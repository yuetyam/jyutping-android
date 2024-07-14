package org.jyutping.jyutping.keyboard

enum class CandidateType {
        Cantonese,
        Text,
        Emoji,
        Symbol,
        Compose;
        fun isCantonese(): Boolean = (this == Cantonese)
        fun isNotCantonese(): Boolean = (this != Cantonese)
}
