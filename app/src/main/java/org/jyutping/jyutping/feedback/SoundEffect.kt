package org.jyutping.jyutping.feedback

import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Keyboard Audio Feedback
 */
enum class SoundEffect(val soundId: Int) {

        @RequiresApi(Build.VERSION_CODES.S)
        Back(AudioManager.FX_BACK),

        Click(AudioManager.FX_KEY_CLICK),
        Delete(AudioManager.FX_KEYPRESS_DELETE),
        Input(AudioManager.FX_KEYPRESS_STANDARD),
        Return(AudioManager.FX_KEYPRESS_RETURN),
        Space(AudioManager.FX_KEYPRESS_SPACEBAR)
}
