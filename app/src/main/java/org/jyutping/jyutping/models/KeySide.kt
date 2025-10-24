package org.jyutping.jyutping.models

/** Represents the location of an input key on the screen — either the left or right half. */
enum class KeySide {

        /** The key is located on the left half of the screen. That is, leading. */
        Left,

        /** The key is located on the right half of the screen. That is, trailing. */
        Right;

        /** Indicates whether the key is on the left half of the screen. */
        val isLeft: Boolean
                get() = (this == Left)

        /** Indicates whether the key is on the right half of the screen. */
        val isRight: Boolean
                get() = (this == Right)
}
