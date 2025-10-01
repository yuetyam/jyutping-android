package org.jyutping.jyutping.keyboard

enum class QwertyForm {

        /// Alphabetic, English
        Abc,

        /// Alphabetic, Cantonese (粵拼全鍵盤)
        Jyutping,

        /** Cantonese Triple-Stroke 粵拼三拼 */
        TripleStroke,

        /** 普通話拼音 */
        Pinyin,

        /** 倉頡或速成 */
        Cangjie,

        /** 筆畫 */
        Stroke,

        /** 拆字、兩分反查. 例如 木 + 木 = 林: mukmuk */
        Structure
}
