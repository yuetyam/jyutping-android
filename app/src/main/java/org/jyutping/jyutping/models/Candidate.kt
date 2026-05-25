package org.jyutping.jyutping.models

import org.jyutping.jyutping.extensions.isCantoneseToneDigit

/** Display Candidate */
data class Candidate(

        /** Cantonese word, emoji, symbol, or plain text */
        val text: String,

        /** Romanization (Jyutping) or annotation */
        val comment: String? = null,

        /** Internal candidate lexicon */
        val lexicon: Lexicon,

        /** Unique number to differentiate every key event */
        val sessionState: Long
) {

        val isCantonese: Boolean
                get() = lexicon.isCantonese

        val isNotCantonese: Boolean
                get() = lexicon.isNotCantonese

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Candidate) return false
                return (sessionState == other.sessionState) && (lexicon.type == other.lexicon.type) && (text == other.text) && (comment == other.comment)
        }

        override fun hashCode(): Int = text.hashCode() * 31 + comment.hashCode()

        /**
         * Create a Candidate from the given text and Lexicon.
         * @param text Candidate display text.
         * @param lexicon Internal candidate lexicon.
         * @param commentForm Romanization display form.
         * @param sessionState Unique number to differentiate every key event
         * @return Candidate
         */
        constructor(
                text: String? = null,
                lexicon: Lexicon,
                commentForm: RomanizationForm,
                sessionState: Long
        ) : this(
                text = text ?: lexicon.text,
                comment = if (lexicon.isNotCantonese) null else when (commentForm) {
                        RomanizationForm.Full -> lexicon.romanization
                        RomanizationForm.Toneless -> lexicon.romanization.filterNot { it.isCantoneseToneDigit }
                        RomanizationForm.Nothing -> null
                },
                lexicon = lexicon,
                sessionState = sessionState
        )
}
