package org.jyutping.jyutping.stroke

/** Keyboard Layout for Stroke Reverse Lookup */
enum class StrokeLayout(val identifier: Int) {

        /** QWERTY layout */
        Default(1),

        /** 10-key layout */
        NineKey(2);

        /** 10-key layout */
        val isNineKey: Boolean
                get() = (this == NineKey)
}
