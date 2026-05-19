package org.jyutping.jyutping.models

enum class KeyboardForm {
        Alphabetic,
        CandidateBoard,
        DecimalPad,
        EditingPanel,
        EmojiBoard,
        LayoutPicker,
        NineKeyNumeric,
        NineKeyStroke,
        NumberPad,
        Numeric,
        Settings,
        Symbolic;

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
