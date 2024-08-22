package org.jyutping.jyutping.keyboard

/// 倉頡／速成版本
enum class CangjieVariant {

        /// 倉頡五代
        Cangjie5,

        /// 倉頡三代
        Cangjie3,

        /// 速成（倉頡五代）
        Quick5,

        /// 速成（倉頡三代）
        Quick3;

        fun identifier(): Int = when (this) {
                Cangjie5 -> 11
                Cangjie3 -> 12
                Quick5 -> 21
                Quick3 -> 22
        }
        companion object {
                fun variantOf(value: Int): CangjieVariant {
                        return when (value) {
                                Cangjie5.identifier() -> Cangjie5
                                Cangjie3.identifier() -> Cangjie3
                                Quick5.identifier() -> Quick5
                                Quick3.identifier() -> Quick3
                                else -> Cangjie5
                        }
                }
        }
}
