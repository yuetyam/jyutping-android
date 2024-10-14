package org.jyutping.jyutping.keyboard

enum class CommentStyle(val identifier: Int) {

        AboveCandidates(1),
        BelowCandidates(2),
        NoComments(3);

        fun isAbove(): Boolean = (this == AboveCandidates)
        fun isBelow(): Boolean = (this == BelowCandidates)
        fun isNone(): Boolean = (this == NoComments)

        companion object {
                fun styleOf(value: Int): CommentStyle = when (value) {
                        AboveCandidates.identifier -> AboveCandidates
                        BelowCandidates.identifier -> BelowCandidates
                        NoComments.identifier -> NoComments
                        else -> AboveCandidates
                }
        }
}
