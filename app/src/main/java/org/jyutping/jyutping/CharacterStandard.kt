package org.jyutping.jyutping

/** Chinese Character Set. 漢字字符集標準／字形標準 */
enum class CharacterStandard(val identifier: Int) {

        /** Traditional (Default). 傳統漢字(預設) */
        Preset(1),

        /** Traditional, Custom Variant. 傳統漢字・自定轉換字符集 */
        Custom(2),

        /** Traditional, Inherited, Old School. 傳統漢字・舊字形・傳承字形 */
        Inherited(3),

        /** Traditional, Philology, Grammatology. 傳統漢字・字源、字理、文字學 */
        Etymology(4),

        /** Traditional, OpenCC. 傳統漢字・OpenCC 字表 */
        OpenCC(5),

        /** Traditional, Hong Kong. 傳統漢字・香港《常用字字形表》 */
        HongKong(6),

        /** Traditional, Taiwan. 傳統漢字・臺灣《國字標準字體表》 */
        Taiwan(7),

        /** Traditional, PRC Mainland. 傳統漢字・大陸《通用規範漢字表》 */
        PrcGeneral(8),

        /** Traditional, PRC Mainland. 傳統漢字・大陸《古籍印刷通用字規範字形表》 */
        AncientBooksPublishing(9),

        /** Simplified, PRC Mainland. 簡化字・大陸《通用規範漢字表》 */
        Mutilated(51);

        /** isSimplified */
        val isMutilated: Boolean
                get() = (this == Mutilated)

        /** isNotSimplified */
        val isTraditional: Boolean
                get() = (this != Mutilated)

        companion object {
                fun standardOf(value: Int): CharacterStandard = entries.find { it.identifier == value } ?: Preset
        }
}
