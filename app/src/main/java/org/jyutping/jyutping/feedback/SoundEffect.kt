package org.jyutping.jyutping.feedback

import android.media.AudioManager
import android.os.Build

/**
 * Keyboard Audio Feedback
 */
enum class SoundEffect {

        Back,
        Click,
        Delete,
        Input,
        Return,
        Space;

        fun soundId(): Int = when (this) {
                Back -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) AudioManager.FX_BACK else AudioManager.FX_KEY_CLICK
                Click -> AudioManager.FX_KEY_CLICK
                Delete -> AudioManager.FX_KEYPRESS_DELETE
                Input -> AudioManager.FX_KEYPRESS_STANDARD
                Return -> AudioManager.FX_KEYPRESS_RETURN
                Space -> AudioManager.FX_KEYPRESS_SPACEBAR
        }
}
