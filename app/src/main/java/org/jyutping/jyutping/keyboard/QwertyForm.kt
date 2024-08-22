package org.jyutping.jyutping.keyboard

enum class QwertyForm {

        /// Alphabetic, English
        Abc,

        /// Alphabetic, Cantonese (粵拼全鍵盤)
        Jyutping,

        /// Cantonese SaamPing (粵拼三拼)
        TripleStroke,

        Pinyin,

        /// Cangjie or Quick(Sucheng)
        Cangjie,

        Stroke,

        /// LoengFan Reverse Lookup. 拆字、兩分反查. 例如 木 + 旦 = 查: mukdaan
        Structure
}
