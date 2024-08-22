package org.jyutping.jyutping.utilities

object ShapeKeyMap {

        fun cangjieCode(char: Char): Char? {
                return cangjieMap[char]
        }
        private val cangjieMap: HashMap<Char, Char> = hashMapOf(
                'a' to '日',
                'b' to '月',
                'c' to '金',
                'd' to '木',
                'e' to '水',
                'f' to '火',
                'g' to '土',
                'h' to '竹',
                'i' to '戈',
                'j' to '十',
                'k' to '大',
                'l' to '中',
                'm' to '一',
                'n' to '弓',
                'o' to '人',
                'p' to '心',
                'q' to '手',
                'r' to '口',
                's' to '尸',
                't' to '廿',
                'u' to '山',
                'v' to '女',
                'w' to '田',
                'x' to '難',
                'y' to '卜',
                'z' to '重'
        )
}
