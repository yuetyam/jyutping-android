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
                if (sessionState != other.sessionState) return false
                return if (isCantonese && other.isCantonese && (comment == null)) {
                        (text == other.text) && (lexicon.toneFreeRomanization == other.lexicon.toneFreeRomanization)
                } else {
                        (text == other.text) && (comment == other.comment)
                }
        }
        override fun hashCode(): Int {
                if (isCantonese && (comment == null)) {
                        var result = sessionState.hashCode()
                        result = 31 * result + text.hashCode()
                        result = 31 * result + lexicon.toneFreeRomanization.hashCode()
                        return result
                } else {
                        var result = sessionState.hashCode()
                        result = 31 * result + text.hashCode()
                        result = 31 * result + (comment?.hashCode() ?: 0)
                        return result
                }
        }

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
                        RomanizationForm.Toneless -> lexicon.toneFreeRomanization
                        RomanizationForm.Nothing -> null
                },
                lexicon = lexicon,
                sessionState = sessionState
        )

        companion object {
                val sample: Candidate = Candidate(text = "例", comment = "lai6", lexicon = Lexicon.sample, sessionState = 9527)
        }
}
