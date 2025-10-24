package org.jyutping.jyutping.models

enum class KeyboardForm {
        Alphabetic,
        CandidateBoard,
        DecimalPad,
        EditingPanel,
        EmojiBoard,
        NumberPad,
        Numeric,
        Settings,
        Symbolic,
        TenKeyNumeric;

        val isBufferable: Boolean
                get() = when (this) {
                        Alphabetic, CandidateBoard -> true
                        else -> false
                }
}
