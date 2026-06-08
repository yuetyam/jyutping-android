package org.jyutping.jyutping.models

enum class KeyboardForm {

        /** Expanded Candidate page */
        CandidateBoard,

        /** 10-key keypad-style digit keyboard with a symbol sidebar and some other action keys */
        DedicatedNumbers,

        /** 9-key (T9) Stroke keyboard for reverse lookup */
        DedicatedStroke,

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
        Symbolic;

        /** Should stay buffering, should keep the bufferText */
        val isBufferable: Boolean
                get() = when (this) {
                        Primary, CandidateBoard, DedicatedStroke -> true
                        else -> false
                }

        /** Main keyboard; alphabetic; letters or 9-key (T9) */
        val isPrimary: Boolean
                get() = (this == Primary)

        /** 10-key keypad-style digit keyboard with a symbol sidebar and some other keys */
        val isDedicatedNumbers: Boolean
                get() = (this == DedicatedNumbers)

        /** 9-key (T9) Stroke keyboard for reverse lookup */
        val isDedicatedStroke: Boolean
                get() = (this == DedicatedStroke)
}
