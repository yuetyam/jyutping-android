package org.jyutping.jyutping.stroke

/** Keyboard Layout for Stroke Reverse Lookup */
enum class StrokeLayout(val identifier: Int) {

        /** QWERTY layout */
        Default(1),

        /** 9-key (T9) layout */
        Tailored(2);

        /** 9-key (T9) layout */
        val isTailored: Boolean
                get() = (this == Tailored)
}
