package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.jyutping.jyutping.models.InputKeyEvent

@Composable
fun NumberKeyRow(height: Dp) {
        Row(
                modifier = Modifier
                        .height(height)
                        .fillMaxWidth()
        ) {
                NumberKey(InputKeyEvent.number1, modifier = Modifier.weight(1f), position = Alignment.Start)
                NumberKey(InputKeyEvent.number2, modifier = Modifier.weight(1f))
                NumberKey(InputKeyEvent.number3, modifier = Modifier.weight(1f))
                NumberKey(InputKeyEvent.number4, modifier = Modifier.weight(1f))
                NumberKey(InputKeyEvent.number5, modifier = Modifier.weight(1f))
                NumberKey(InputKeyEvent.number6, modifier = Modifier.weight(1f))
                NumberKey(InputKeyEvent.number7, modifier = Modifier.weight(1f))
                NumberKey(InputKeyEvent.number8, modifier = Modifier.weight(1f))
                NumberKey(InputKeyEvent.number9, modifier = Modifier.weight(1f))
                NumberKey(InputKeyEvent.number0, modifier = Modifier.weight(1f), position = Alignment.End)
        }
}
