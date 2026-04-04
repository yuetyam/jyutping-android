package org.jyutping.jyutping.models

object CangjieConverter {

        /**
         * Convert the given letter VirtualInputKey to a Cangjie root character
         * - Parameter key: Letter VirtualInputKey [a-z]
         * - Returns: Cangjie root / radical character
         */
        fun cangjieOf(key: VirtualInputKey): String? = cangjieKeyMap[key]
        private val cangjieKeyMap: Map<VirtualInputKey, String> = mapOf(
                VirtualInputKey.letterA to "日",
                VirtualInputKey.letterB to "月",
                VirtualInputKey.letterC to "金",
                VirtualInputKey.letterD to "木",
                VirtualInputKey.letterE to "水",
                VirtualInputKey.letterF to "火",
                VirtualInputKey.letterG to "土",
                VirtualInputKey.letterH to "竹",
                VirtualInputKey.letterI to "戈",
                VirtualInputKey.letterJ to "十",
                VirtualInputKey.letterK to "大",
                VirtualInputKey.letterL to "中",
                VirtualInputKey.letterM to "一",
                VirtualInputKey.letterN to "弓",
                VirtualInputKey.letterO to "人",
                VirtualInputKey.letterP to "心",
                VirtualInputKey.letterQ to "手",
                VirtualInputKey.letterR to "口",
                VirtualInputKey.letterS to "尸",
                VirtualInputKey.letterT to "廿",
                VirtualInputKey.letterU to "山",
                VirtualInputKey.letterV to "女",
                VirtualInputKey.letterW to "田",
                VirtualInputKey.letterX to "難",
                VirtualInputKey.letterY to "卜",
                VirtualInputKey.letterZ to "重"
        )
}
