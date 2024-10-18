package org.jyutping.jyutping.editingpanel

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.PresetColor
import splitties.systemservices.windowManager
import kotlin.math.min

@Composable
fun EditingPanel(height: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode = remember { context.isDarkMode }
        val screenWidth: Float = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val windowMetrics = context.windowManager.currentWindowMetrics
                (windowMetrics.bounds.width() / windowMetrics.density)
        } else {
                LocalConfiguration.current.screenWidthDp.toFloat()
        }
        val edgeKeyWidth: Float = min(135f, screenWidth / 4.0f)
        val edgeKeyWeight: Float = edgeKeyWidth / screenWidth
        val middleWeight: Float = 1f - (edgeKeyWeight * 2f)
        val clipboardKeyWeight: Float = middleWeight / 2.0f
        val arrowKeyWeight: Float = middleWeight / 3.0f
        val keyHeight: Dp = height / 4.0f
        Row(
                modifier = Modifier
                        .background(if (isDarkMode.value) PresetColor.keyboardDarkBackground else PresetColor.keyboardLightBackground)
                        .systemBarsPadding()
                        .height(height)
                        .fillMaxWidth()
        ) {
                Column(
                        modifier = Modifier
                                .weight(edgeKeyWeight)
                                .fillMaxSize()
                ) {
                        EditingPanelCopyKey(modifier = Modifier.height(keyHeight))
                        EditingPanelCutKey(modifier = Modifier.height(keyHeight))
                        EditingPanelClearKey(modifier = Modifier.height(keyHeight))
                        EditingPanelConversionKey(modifier = Modifier.height(keyHeight))
                }
                Column(
                        modifier = Modifier
                                .weight(clipboardKeyWeight * 2f)
                                .fillMaxSize()
                ) {
                        Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                EditingPanelClearClipboardKey(modifier = Modifier.weight(clipboardKeyWeight).height(keyHeight))
                                EditingPanelPasteKey(modifier = Modifier.weight(clipboardKeyWeight).height(keyHeight))
                        }
                        Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                EditingPanelMoveBackwardKey(modifier = Modifier.weight(arrowKeyWeight).height(keyHeight))
                                EditingPanelMoveUpwardKey(modifier = Modifier.weight(arrowKeyWeight).height(keyHeight))
                                EditingPanelMoveForwardKey(modifier = Modifier.weight(arrowKeyWeight).height(keyHeight))
                        }
                        Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                EditingPanelJump2HeadKey(modifier = Modifier.weight(arrowKeyWeight).height(keyHeight))
                                EditingPanelMoveDownwardKey(modifier = Modifier.weight(arrowKeyWeight).height(keyHeight))
                                EditingPanelJump2TailKey(modifier = Modifier.weight(arrowKeyWeight).height(keyHeight))
                        }
                        EditingPanelSpaceKey(modifier = Modifier.height(keyHeight))
                }
                Column(
                        modifier = Modifier
                                .weight(edgeKeyWeight)
                                .fillMaxSize()
                ) {
                        EditingPanelBackKey(modifier = Modifier.height(keyHeight))
                        EditingPanelBackspaceKey(modifier = Modifier.height(keyHeight))
                        EditingPanelForwardDeleteKey(modifier = Modifier.height(keyHeight))
                        EditingPanelReturnKey(modifier = Modifier.height(keyHeight))
                }
        }
}
