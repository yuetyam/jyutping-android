package org.jyutping.jyutping.models

/** CommentForm. Text form of Candidate jyutping syllables. */
enum class RomanizationForm(val identifier: Int) {

        /** Regular */
        Full(1),

        /** Tone-free */
        Toneless(2),

        /** Empty */
        Nothing(3);
}
