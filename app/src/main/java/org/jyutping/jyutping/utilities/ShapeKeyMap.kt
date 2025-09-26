package org.jyutping.jyutping.utilities

object ShapeKeyMap {
        fun cangjieCode(letter: String): String? = cangjieMap[letter]
        private val cangjieMap: HashMap<String, String> = hashMapOf(
                "a" to "日",
                "b" to "月",
                "c" to "金",
                "d" to "木",
                "e" to "水",
                "f" to "火",
                "g" to "土",
                "h" to "竹",
                "i" to "戈",
                "j" to "十",
                "k" to "大",
                "l" to "中",
                "m" to "一",
                "n" to "弓",
                "o" to "人",
                "p" to "心",
                "q" to "手",
                "r" to "口",
                "s" to "尸",
                "t" to "廿",
                "u" to "山",
                "v" to "女",
                "w" to "田",
                "x" to "難",
                "y" to "卜",
                "z" to "重"
        )

        fun strokeCode(char: Char): Char? = strokeMap[char]
        private val strokeMap: HashMap<Char, Char> = hashMapOf(
                'w' to '⼀',
                's' to '⼁',
                'a' to '⼃',
                'd' to '⼂',
                'z' to '乛',
                'x' to '＊'
        )

        fun keyStroke(letter: String): String? = keyStrokeMap[letter]
        private val keyStrokeMap: HashMap<String, String> = hashMapOf(
                "w" to "⼀",
                "s" to "⼁",
                "a" to "⼃",
                "d" to "⼂",
                "z" to "乛",
                "x" to "＊",

                "j" to "⼀",
                "k" to "⼁",
                "l" to "⼃",
                "u" to "⼂",
                "i" to "乛",
                "o" to "＊"
        )

        fun strokeTransform(text: String): String = text.mapNotNull { mapStrokeKey[it] }.joinToString(separator = "")
        private val mapStrokeKey: HashMap<Char, Char> = hashMapOf(
                'w' to 'w',
                'h' to 'w',
                't' to 'w',
                's' to 's',
                'a' to 'a',
                'p' to 'a',
                'd' to 'd',
                'n' to 'd',
                'z' to 'z',
                'x' to 'x',
                '*' to 'x',

                'j' to 'w',
                'k' to 's',
                'l' to 'a',
                'u' to 'd',
                'i' to 'z',
                'o' to 'x',

                '1' to 'w',
                '2' to 's',
                '3' to 'a',
                '4' to 'd',
                '5' to 'z',
                '6' to 'x'
        )
}

// 橫: w, h, t: w = Waang, h = Héng, t = 提 = Tai = Tí
// 豎: s      : s = Syu = Shù
// 撇: a, p   : p = Pit = Piě
// 點: d, n   : d = Dim = Diǎn, n = 捺 = Naat = Nà
// 折: z      : z = Zit = Zhé
// 通: x, *   : x = wildcard match
//
// macOS built-in Stroke: https://support.apple.com/zh-hk/guide/chinese-input-method/cim4f6882a80/mac
// 橫: j, KP_1
// 豎: k, KP_2
// 撇: l, KP_3
// 點: u, KP_4
// 折: i, KP_5
// 通: o, KP_6
