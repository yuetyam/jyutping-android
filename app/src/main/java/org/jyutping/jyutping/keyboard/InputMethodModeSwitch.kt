package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.extensions.keyLight
import org.jyutping.jyutping.extensions.keyLightEmphatic

@Composable
fun InputMethodModeSwitch() {
        val context = LocalContext.current as JyutpingInputMethodService
        val inputMethodMode = remember { context.inputMethodMode }
        val shapeWidth: Dp = 72.dp
        val shapeHeight: Dp = 25.dp
        val partWidth: Dp = 36.dp
        val cornerRadius: Dp = 5.dp
        val largerFontSize = 17.sp
        val normalFontSize = 14.sp
        Box(
                modifier = Modifier
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(Color.keyLightEmphatic)
                        .width(shapeWidth)
                        .height(shapeHeight),
                contentAlignment = Alignment.Center
        ) {
                Row(
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Box(
                                modifier = Modifier
                                        .clip(RoundedCornerShape(cornerRadius))
                                        .background(if (inputMethodMode.value.isCantonese()) Color.keyLight else Color.Transparent)
                                        .width(partWidth)
                                        .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = "粵",
                                        fontSize = if (inputMethodMode.value.isCantonese()) largerFontSize else normalFontSize
                                )
                        }
                        Box(
                                modifier = Modifier
                                        .clip(RoundedCornerShape(cornerRadius))
                                        .background(if (inputMethodMode.value.isABC()) Color.keyLight else Color.Transparent)
                                        .width(partWidth)
                                        .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = "A",
                                        fontSize = if (inputMethodMode.value.isABC()) largerFontSize else normalFontSize
                                )
                        }
                }
        }
}
