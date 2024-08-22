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

        fun strokeCode(char: Char): Char? {
                return strokeMap[char]
        }
        private val strokeMap: HashMap<Char, Char> = hashMapOf(
                'w' to '⼀',
                's' to '⼁',
                'a' to '⼃',
                'd' to '⼂',
                'z' to '⼄'
        )

        // 橫: w, h, t: w = Waang, h = Héng, t = 提 = Tai = Tí
        // 豎: s      : s = Syu = Shù
        // 撇: a, p   : p = Pit = Piě
        // 點: d, n   : d = Dim = Diǎn, n = 捺 = Naat = Nà
        // 折: z      : z = Zit = Zhé
        //
        // macOS built-in Stroke: https://support.apple.com/zh-hk/guide/chinese-input-method/cimskt12969/mac
        // 橫: j, KP_1
        // 豎: k, KP_2
        // 撇: l, KP_3
        // 點: u, KP_4
        // 折: i, KP_5
        fun strokeTransform(text: String): String = text
                .replace(Regex("[htj1]"), "w")
                .replace(Regex("[k2]"), "s")
                .replace(Regex("[pl3]"), "a")
                .replace(Regex("[nu4]"), "d")
                .replace(Regex("[i5]"), "z")
}
