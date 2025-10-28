package org.jyutping.jyutping

/** Character Set. 字符集. 字形標準 */
enum class CharacterStandard(val identifier: Int) {

        /** 傳統漢字 */
        Traditional(1),

        /** 傳統漢字・香港 */
        HongKong(2),

        /** 傳統漢字・臺灣 */
        Taiwan(3),

        /** 簡化字 */
        Simplified(4);

        /** 簡化字 */
        val isSimplified: Boolean
                get() = (this == Simplified)

        companion object {
                fun standardOf(value: Int): CharacterStandard = entries.find { it.identifier == value } ?: Traditional
        }
}
