package org.jyutping.jyutping.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

@Composable
fun AppLinkLabel(icon: ImageVector, text: String, uri: String) {
        val uriHandler = LocalUriHandler.current
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorScheme.background)
                        .clickable { uriHandler.openUri(uri) }
                        .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colorScheme.onBackground
                )
                Text(
                        text = text,
                        color = colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1.0f))
                Icon(
                        imageVector = Icons.Outlined.ArrowOutward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp).alpha(0.75f),
                        tint = colorScheme.onBackground
                )
        }
}
