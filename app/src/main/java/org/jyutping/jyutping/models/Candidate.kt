package org.jyutping.jyutping.models

import org.jyutping.jyutping.CharacterStandard
import org.jyutping.jyutping.extensions.isCantoneseToneDigit
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.HongKongVariantConverter
import org.jyutping.jyutping.utilities.Simplifier
import org.jyutping.jyutping.utilities.TaiwanVariantConverter

/** Display Candidate */
data class Candidate(

        /** Cantonese word, emoji, symbol, or plain text */
        val text: String,

        /** Romanization (Jyutping) or annotation */
        val comment: String? = null,

        /** Internal candidate lexicon */
        val lexicon: Lexicon
) {

        val isCantonese: Boolean
                get() = lexicon.isCantonese

        val isNotCantonese: Boolean
                get() = lexicon.isNotCantonese

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Candidate) return false
                if (lexicon.type != other.lexicon.type) return false
                return (text == other.text) && (comment == other.comment)
        }

        override fun hashCode(): Int = text.hashCode() * 31 + comment.hashCode()

        /**
         * Create a Candidate from the given text and Lexicon.
         * @param lexicon Internal candidate lexicon.
         * @param commentForm Romanization display form.
         * @param charset CharacterStandard for Lexicon text conversion.
         * @param db DatabaseHelper to simplify Lexicon text.
         * @return Candidate
         */
        constructor(
                lexicon: Lexicon,
                commentForm: RomanizationForm,
                charset: CharacterStandard = CharacterStandard.Traditional,
                db: DatabaseHelper? = null
        ) : this(
                text = if (lexicon.isNotCantonese) lexicon.text else when (charset) {
                        CharacterStandard.Traditional -> lexicon.text
                        CharacterStandard.HongKong -> HongKongVariantConverter.convert(lexicon.text)
                        CharacterStandard.Taiwan -> TaiwanVariantConverter.convert(lexicon.text)
                        CharacterStandard.Simplified -> if (db != null) Simplifier.convert(lexicon.text, db) else lexicon.text
                },
                comment = if (lexicon.isNotCantonese) null else when (commentForm) {
                        RomanizationForm.Full -> lexicon.romanization
                        RomanizationForm.Toneless -> lexicon.romanization.filterNot { it.isCantoneseToneDigit }
                        RomanizationForm.Nothing -> null
                },
                lexicon = lexicon
        )
}
