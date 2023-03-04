package org.jyutping.jyutping.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun Label(icon: ImageVector, text: String, symbol: ImageVector, onClick: () -> Unit = {}) {
        Row(
                modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        .clickable { onClick() }
                        .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Icon(imageVector = icon, contentDescription = null)
                Text(text = text)
                Spacer(modifier = Modifier.weight(1.0f))
                Icon(imageVector = symbol, contentDescription = null)
        }
}
