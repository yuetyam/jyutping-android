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
        NineKeyNumeric,
        NineKeyStroke;

        val isBufferable: Boolean
                get() = when (this) {
                        Alphabetic, CandidateBoard, NineKeyStroke -> true
                        else -> false
                }

        val isNineKeyNumeric: Boolean
                get() = (this == NineKeyNumeric)

        val isNineKeyStroke: Boolean
                get() = (this == NineKeyStroke)
}
