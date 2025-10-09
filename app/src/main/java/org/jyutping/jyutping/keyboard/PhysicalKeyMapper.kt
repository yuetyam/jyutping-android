package org.jyutping.jyutping.keyboard

import android.view.KeyEvent
import org.jyutping.jyutping.models.InputKeyEvent

/**
 * Map Android KeyEvent keyCode to the internal InputKeyEvent used by the IME.
 * Keep mapping small and ASCII-only for the first pass.
 */
object PhysicalKeyMapper {

    private val mapping: Map<Int, InputKeyEvent> = mapOf(
        KeyEvent.KEYCODE_A to InputKeyEvent.letterA,
        KeyEvent.KEYCODE_B to InputKeyEvent.letterB,
        KeyEvent.KEYCODE_C to InputKeyEvent.letterC,
        KeyEvent.KEYCODE_D to InputKeyEvent.letterD,
        KeyEvent.KEYCODE_E to InputKeyEvent.letterE,
        KeyEvent.KEYCODE_F to InputKeyEvent.letterF,
        KeyEvent.KEYCODE_G to InputKeyEvent.letterG,
        KeyEvent.KEYCODE_H to InputKeyEvent.letterH,
        KeyEvent.KEYCODE_I to InputKeyEvent.letterI,
        KeyEvent.KEYCODE_J to InputKeyEvent.letterJ,
        KeyEvent.KEYCODE_K to InputKeyEvent.letterK,
        KeyEvent.KEYCODE_L to InputKeyEvent.letterL,
        KeyEvent.KEYCODE_M to InputKeyEvent.letterM,
        KeyEvent.KEYCODE_N to InputKeyEvent.letterN,
        KeyEvent.KEYCODE_O to InputKeyEvent.letterO,
        KeyEvent.KEYCODE_P to InputKeyEvent.letterP,
        KeyEvent.KEYCODE_Q to InputKeyEvent.letterQ,
        KeyEvent.KEYCODE_R to InputKeyEvent.letterR,
        KeyEvent.KEYCODE_S to InputKeyEvent.letterS,
        KeyEvent.KEYCODE_T to InputKeyEvent.letterT,
        KeyEvent.KEYCODE_U to InputKeyEvent.letterU,
        KeyEvent.KEYCODE_V to InputKeyEvent.letterV,
        KeyEvent.KEYCODE_W to InputKeyEvent.letterW,
        KeyEvent.KEYCODE_X to InputKeyEvent.letterX,
        KeyEvent.KEYCODE_Y to InputKeyEvent.letterY,
        KeyEvent.KEYCODE_Z to InputKeyEvent.letterZ,

        KeyEvent.KEYCODE_0 to InputKeyEvent.number0,
        KeyEvent.KEYCODE_1 to InputKeyEvent.number1,
        KeyEvent.KEYCODE_2 to InputKeyEvent.number2,
        KeyEvent.KEYCODE_3 to InputKeyEvent.number3,
        KeyEvent.KEYCODE_4 to InputKeyEvent.number4,
        KeyEvent.KEYCODE_5 to InputKeyEvent.number5,
        KeyEvent.KEYCODE_6 to InputKeyEvent.number6,
        KeyEvent.KEYCODE_7 to InputKeyEvent.number7,
        KeyEvent.KEYCODE_8 to InputKeyEvent.number8,
        KeyEvent.KEYCODE_9 to InputKeyEvent.number9,

        KeyEvent.KEYCODE_APOSTROPHE to InputKeyEvent.apostrophe,
        KeyEvent.KEYCODE_GRAVE to InputKeyEvent.grave
    )

    fun map(keyCode: Int): InputKeyEvent? = mapping[keyCode]

    fun isPrintable(keyCode: Int): Boolean = mapping.containsKey(keyCode)
}
