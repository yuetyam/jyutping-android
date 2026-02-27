package org.jyutping.jyutping.models

enum class LexiconType {

        Cantonese,

        /** Plain text. Examples: Face ID, SwiftUI, Café */
        Text,

        /** Note that `Lexicon.text.count == 1` not always true for this type. */
        Emoji,

        /** Note that `Lexicon.text.count == 1` not always true for this type. */
        Symbol,

        /** External physics keyboard composed text. Mainly for PunctuationKey. */
        Composed;

        val isCantonese: Boolean
                get() = (this == Cantonese)

        val isNotCantonese: Boolean
                get() = (this != Cantonese)
}
