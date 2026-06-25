package org.jyutping.jyutping.feedback

import android.media.AudioManager

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

        val soundId: Int
                get() = when (this) {
                        Back   -> AudioManager.FX_BACK
                        Click  -> AudioManager.FX_KEY_CLICK
                        Delete -> AudioManager.FX_KEYPRESS_DELETE
                        Input  -> AudioManager.FX_KEYPRESS_STANDARD
                        Return -> AudioManager.FX_KEYPRESS_RETURN
                        Space  -> AudioManager.FX_KEYPRESS_SPACEBAR
                }
}
