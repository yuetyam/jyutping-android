package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.models.KeyboardForm

@Composable
fun BottomKeyRow(transform: KeyboardForm, height: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val needsInputModeSwitchKey by context.needsInputModeSwitchKey.collectAsState()
        val needsLeftKey by context.needsLeftKey.collectAsState()
        val needsRightKey by context.needsRightKey.collectAsState()
        val enabledBottomKeyCount: Int = listOf(needsInputModeSwitchKey, needsLeftKey, needsRightKey).count { it }
        val edgeBottomKeyWeight: Float = when (enabledBottomKeyCount) {
                0 -> 2f
                1 -> 2f
                2 -> 2f
                3 -> 1.5f
                else -> 2f
        }
        val spaceKeyWeight: Float = when (enabledBottomKeyCount) {
                0 -> 6f
                1 -> 5f
                2 -> 4f
                3 -> 4f
                else -> 4f
        }
        Row(
                modifier = Modifier
                        .height(height)
                        .fillMaxWidth()
        ) {
                TransformKey(destination = transform, modifier = Modifier.weight(edgeBottomKeyWeight))
                if (needsLeftKey) {
                        LeftKey(modifier = Modifier.weight(1f))
                }
                if (needsInputModeSwitchKey) {
                        GlobeKey(modifier = Modifier.weight(1f))
                }
                SpaceKey(modifier = Modifier.weight(spaceKeyWeight))
                if (needsRightKey) {
                        RightKey(modifier = Modifier.weight(1f))
                }
                ReturnKey(modifier = Modifier.weight(edgeBottomKeyWeight))
        }
}
