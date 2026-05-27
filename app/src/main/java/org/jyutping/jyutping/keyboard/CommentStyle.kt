package org.jyutping.jyutping.keyboard

enum class CommentStyle(val identifier: Int) {

        AboveCandidates(1),
        BelowCandidates(2),
        NoComments(3);

        val isAbove: Boolean
                get() = (this == AboveCandidates)
        val isBelow: Boolean
                get() = (this == BelowCandidates)
        val isNone: Boolean
                get() = (this == NoComments)

        companion object {
                fun styleOf(value: Int): CommentStyle = entries.find { it.identifier == value } ?: AboveCandidates
        }
}
