package org.jyutping.jyutping.models

enum class KeyboardForm {

        /** Expanded Candidate page */
        CandidateBoard,

        /** Button page for copy, cut, clear, etc. */
        EditingPanel,

        /** Emoji keyboard */
        EmojiBoard,

        /** KeyboardLayout picking page */
        LayoutPicker,

        /** Numbers and symbols */
        Numeric,

        /** Main keyboard; alphabetic; letters or 9-key (T9) */
        Primary,

        /** Keyboard settings page */
        Settings,

        /** Extra symbols */
        Symbolic,

        /** 10-key keypad-style digit keyboard with a symbol sidebar and some other keys */
        TailoredNumbers,

        /** 9-key (T9) Stroke keyboard for reverse lookup */
        TailoredStroke;

        /** Should stay buffering, should keep the bufferText */
        val isBufferable: Boolean
                get() = when (this) {
                        Primary, CandidateBoard, TailoredStroke -> true
                        else -> false
                }

        /** Main keyboard; alphabetic; letters or 9-key (T9) */
        val isPrimary: Boolean
                get() = (this == Primary)

        /** 10-key keypad-style digit keyboard with a symbol sidebar and some other keys */
        val isTailoredNumbers: Boolean
                get() = (this == TailoredNumbers)

        /** 9-key (T9) Stroke keyboard for reverse lookup */
        val isTailoredStroke: Boolean
                get() = (this == TailoredStroke)
}
