package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun ShiftKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode = remember { context.isDarkMode }
        var isPressing by remember { mutableStateOf(false) }
        val keyboardCase = remember { context.keyboardCase }
        val drawableId: Int = when (keyboardCase.value) {
                KeyboardCase.Lowercased -> R.drawable.key_shift
                KeyboardCase.Uppercased -> R.drawable.key_shifting
                KeyboardCase.CapsLocked -> R.drawable.key_capslock
        }
        var previousKeyboardCase by remember { mutableStateOf(KeyboardCase.Lowercased) }
        var isInTheMediumOfDoubleTapping by remember { mutableStateOf(false) }
        var doubleTappingBuffer by remember { mutableIntStateOf(0) }
        LaunchedEffect(isInTheMediumOfDoubleTapping) {
                while (isInTheMediumOfDoubleTapping) {
                        delay(100L) // 0.1s
                        if (doubleTappingBuffer >= 3) {
                                doubleTappingBuffer = 0
                                isInTheMediumOfDoubleTapping = false
                        } else {
                                doubleTappingBuffer += 1
                        }
                }
        }
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onPress = {
                                                isPressing = true
                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                tryAwaitRelease()
                                                isPressing = false
                                        },
                                        onTap = {
                                                val currentKeyboardCase = keyboardCase.value
                                                val didKeyboardCaseSwitchBack = (currentKeyboardCase == previousKeyboardCase)
                                                val shouldPerformDoubleTapping = isInTheMediumOfDoubleTapping && didKeyboardCaseSwitchBack.not()
                                                doubleTappingBuffer = 0
                                                previousKeyboardCase = currentKeyboardCase
                                                if (shouldPerformDoubleTapping) {
                                                        isInTheMediumOfDoubleTapping = false
                                                        context.doubleShift()
                                                } else {
                                                        isInTheMediumOfDoubleTapping = true
                                                        context.shift()
                                                }
                                        }
                                )
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box(
                        modifier = modifier
                                .padding(horizontal = 3.dp, vertical = 6.dp)
                                .shadow(
                                        elevation = 0.5.dp,
                                        shape = RoundedCornerShape(6.dp)
                                )
                                .background(
                                        if (isDarkMode.value) {
                                                if (isPressing) PresetColor.keyDark else PresetColor.keyDarkEmphatic
                                        } else {
                                                if (isPressing) PresetColor.keyLight else PresetColor.keyLightEmphatic
                                        }
                                )
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = drawableId),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = if (isDarkMode.value) Color.White else Color.Black
                        )
                }
        }
}
