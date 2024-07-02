package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.extensions.keyLight
import org.jyutping.jyutping.extensions.keyLightEmphatic

@Composable
fun ShiftKey(modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed = interactionSource.collectIsPressedAsState()
        val context = LocalContext.current as JyutpingInputMethodService
        val keyboardCase = remember { context.keyboardCase }
        val drawableId: Int = when (keyboardCase.value) {
                KeyboardCase.Lowercased -> R.drawable.key_shift
                KeyboardCase.Uppercased -> R.drawable.key_shifting
                KeyboardCase.CapsLocked -> R.drawable.key_capslock
        }
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onDoubleTap = { context.doubleShift() },
                                        onTap = { context.shift() }
                                )
                        }
                        .fillMaxWidth()
                        .fillMaxHeight(),
                contentAlignment = Alignment.Center
        ) {
                Box (
                        modifier = modifier
                                .padding(horizontal = 3.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isPressed.value) Color.keyLight else Color.keyLightEmphatic)
                                .fillMaxWidth()
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = drawableId),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                        )
                }
        }
}
