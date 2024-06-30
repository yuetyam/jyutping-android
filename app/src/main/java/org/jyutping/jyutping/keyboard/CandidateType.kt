package org.jyutping.jyutping.keyboard

enum class CandidateType {
        Cantonese,
        Text,
        Emoji,
        Symbol,
        Compose
}

fun CandidateType.isCantonese(): Boolean = (this == CandidateType.Cantonese)
