package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.extensions.keyboardLightBackground

@Composable
fun CantoneseSymbolicKeyboard(keyHeight: Dp) {
        Column(
                modifier = Modifier
                        .background(Color.keyboardLightBackground)
                        .fillMaxWidth()
        ) {
                Box(
                        modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                ) {
                        Color.keyboardLightBackground
                        ToolBar()
                }
                Row(
                        modifier = Modifier
                                .height(keyHeight)
                                .fillMaxWidth()
                ) {
                        SymbolKey(symbol = "［", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "］", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "｛", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "｝", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "#", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "%", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "^", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "*", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "+", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "=", modifier = Modifier.weight(1f))
                }
                Row(
                        modifier = Modifier
                                .height(keyHeight)
                                .fillMaxWidth()
                ) {
                        SymbolKey(symbol = "_", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "—", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "\\", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "｜", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "～", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "《", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "》", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "¥", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "&", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "·", modifier = Modifier.weight(1f))
                }
                Row(
                        modifier = Modifier
                                .height(keyHeight)
                                .fillMaxWidth()
                ) {
                        TransformKey(destination = KeyboardForm.Numeric, modifier = Modifier.weight(1.3f))
                        Spacer(modifier = Modifier.weight(0.22f))
                        SymbolKey(symbol = "…", modifier = Modifier.weight(1.16f))
                        SymbolKey(symbol = "，", modifier = Modifier.weight(1.16f))
                        SymbolKey(symbol = "©", modifier = Modifier.weight(1.16f))
                        SymbolKey(symbol = "？", modifier = Modifier.weight(1.16f))
                        SymbolKey(symbol = "！", modifier = Modifier.weight(1.16f))
                        SymbolKey(symbol = "'", modifier = Modifier.weight(1.16f))
                        Spacer(modifier = Modifier.weight(0.22f))
                        BackspaceKey(modifier = Modifier.weight(1.3f))
                }
                Row(
                        modifier = Modifier
                                .height(keyHeight)
                                .fillMaxWidth()
                ) {
                        TransformKey(destination = KeyboardForm.Alphabetic, modifier = Modifier.weight(2f))
                        LeftKey(modifier = Modifier.weight(1f))
                        SpaceKey(modifier = Modifier.weight(4f))
                        RightKey(modifier = Modifier.weight(1f))
                        ReturnKey(modifier = Modifier.weight(2f))
                }
        }
}
