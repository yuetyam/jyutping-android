package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.jyutping.jyutping.models.KeyboardForm

@Composable
fun SimpleBottomKeyRow(transform: KeyboardForm, height: Dp) {
        Row(
                modifier = Modifier
                        .height(height)
                        .fillMaxWidth()
        ) {
                TransformKey(destination = transform, modifier = Modifier.weight(2f))
                SpaceKey(modifier = Modifier.weight(6f))
                ReturnKey(modifier = Modifier.weight(2f))
        }
}
