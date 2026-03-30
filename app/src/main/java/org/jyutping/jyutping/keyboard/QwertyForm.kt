package org.jyutping.jyutping.keyboard

enum class QwertyForm {

        /** ANSI layout for typing English */
        ABC,

        /** ANSI layout for inputting Cantonese */
        Primary,

        /** Cantonese Triple-Stroke. 粵拼三拼 */
        TripleStroke,

        /** ANSI layout for reverse lookup using Mandarin Pinyin. 普通話拼音反查粵拼 */
        Pinyin,

        /** ANSI layout for reverse lookup using Cangjie or Quick. 倉頡或速成反查粵拼 */
        Cangjie,

        /** ANSI layout for reverse lookup using Stroke. 筆畫反查粵拼 */
        Stroke
}
