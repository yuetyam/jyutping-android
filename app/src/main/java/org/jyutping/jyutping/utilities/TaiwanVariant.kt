package org.jyutping.jyutping.utilities

object TaiwanVariant {
        private val charMap: Map<String, String> = mapOf(
                "僞" to "偽",
                "啓" to "啟",
                // "喫" to "吃",
                "嫺" to "嫻",
                "嬀" to "媯",
                "峯" to "峰",
                "幺" to "么",
                "擡" to "抬",
                "棱" to "稜",
                "檐" to "簷",
                "污" to "汙",
                "泄" to "洩",
                "潙" to "溈",
                "潨" to "潀",
                "爲" to "為",
                "牀" to "床",
                "痹" to "痺",
                "癡" to "痴",
                "皁" to "皂",
                "着" to "著",
                "睾" to "睪",
                "祕" to "秘",
                "竈" to "灶",
                "糉" to "粽",
                "繮" to "韁",
                "纔" to "才",
                "羣" to "群",
                "脣" to "唇",
                "蔘" to "參",
                "蔿" to "蒍",
                "衆" to "眾",
                "裏" to "裡",
                "覈" to "核",
                "踊" to "踴",
                "鉢" to "缽",
                "鍼" to "針",
                "鮎" to "鯰",
                "麪" to "麵",
                "齶" to "顎",
        )

        private val codeMap: Map<Int, Int> = charMap.map { it.key.codePointAt(0) to it.value.codePointAt(0) }.toMap()

        /**
         * Convert Chinese characters to the Taiwan CharacterStandard.
         * @param text Original traditional Chinese text (in OpenCC standard).
         * @return Converted text in Taiwan variant.
         */
        fun convert(text: String): String = buildString { text.codePoints().forEachOrdered { appendCodePoint(codeMap[it] ?: it) } }
}
