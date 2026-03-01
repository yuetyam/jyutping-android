package org.jyutping.jyutping.models

import org.jyutping.jyutping.presets.PresetString

/**
 * Stroke Input Event
 * @property code Unique identifier; internal processing code.
 */
enum class StrokeVirtualKey(val code: Int) {

        Horizontal(1),
        Vertical(2),
        LeftFalling(3),
        RightFalling(4),
        Turning(5),
        Wildcard(6);

        val isWildcard: Boolean
                get() = (this == Wildcard)

        val virtualInputKey: VirtualInputKey
                get() = when (this) {
                        Horizontal   -> VirtualInputKey.number1
                        Vertical     -> VirtualInputKey.number2
                        LeftFalling  -> VirtualInputKey.number3
                        RightFalling -> VirtualInputKey.number4
                        Turning      -> VirtualInputKey.number5
                        Wildcard     -> VirtualInputKey.number6
                }

        val strokeText: String?
                get() = displayMap[this]

        companion object {

                fun displayStrokesOf(keys: List<VirtualInputKey>): String = keys.mapNotNull { strokeKeyOf(it)?.strokeText }.joinToString(separator = PresetString.EMPTY)

                fun displayStrokeKeyTextOf(key: VirtualInputKey): String? = keyKeyMap[key]?.strokeText

                private val displayMap: Map<StrokeVirtualKey, String> = mapOf(
                        Horizontal   to "⼀",
                        Vertical     to "⼁",
                        LeftFalling  to "⼃",
                        RightFalling to "⼂",
                        Turning      to "乛",
                        Wildcard     to "＊"
                )

                fun isValidStrokes(keys: List<VirtualInputKey>): Boolean = keys.none { keyKeyMap[it] == null && extraKeyKeyMap[it] == null }

                fun strokeKeyOf(key: VirtualInputKey): StrokeVirtualKey? = keyKeyMap[key] ?: extraKeyKeyMap[key]

                private val keyKeyMap: Map<VirtualInputKey, StrokeVirtualKey> = mapOf(
                        VirtualInputKey.letterW to Horizontal,
                        VirtualInputKey.letterS to Vertical,
                        VirtualInputKey.letterA to LeftFalling,
                        VirtualInputKey.letterD to RightFalling,
                        VirtualInputKey.letterZ to Turning,
                        VirtualInputKey.letterX to Wildcard,

                        VirtualInputKey.letterJ to Horizontal,
                        VirtualInputKey.letterK to Vertical,
                        VirtualInputKey.letterL to LeftFalling,
                        VirtualInputKey.letterU to RightFalling,
                        VirtualInputKey.letterI to Turning,
                        VirtualInputKey.letterO to Wildcard,

                        VirtualInputKey.number1 to Horizontal,
                        VirtualInputKey.number2 to Vertical,
                        VirtualInputKey.number3 to LeftFalling,
                        VirtualInputKey.number4 to RightFalling,
                        VirtualInputKey.number5 to Turning,
                        VirtualInputKey.number6 to Wildcard,
                )
                private val extraKeyKeyMap: Map<VirtualInputKey, StrokeVirtualKey> = mapOf(
                        VirtualInputKey.letterH to Horizontal,
                        VirtualInputKey.letterT to Horizontal,
                        VirtualInputKey.letterP to LeftFalling,
                        VirtualInputKey.letterN to RightFalling,
                )
        }
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
