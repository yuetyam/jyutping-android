package org.jyutping.jyutping.models

/** InputMethodType */
enum class CompositionType {
        /** Jyutping, and reverse lookup using character components. 粵拼以及兩分拆字反查粵拼 */
        Primary,

        /** Reverse lookup using Mandarin Pinyin. 普通話拼音反查粵拼 */
        Pinyin,

        /** Reverse lookup using Cangjie or Quick. 倉頡或速成反查粵拼 */
        Cangjie,

        /** Reverse lookup using Stroke. 筆畫反查粵拼 */
        Stroke;
}
