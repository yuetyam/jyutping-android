package org.jyutping.jyutping.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

@Composable
fun NavigationLabel(icon: ImageVector, text: String, onClick: () -> Unit) {
        Row(
                modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = colorScheme.background)
                        .clickable { onClick() }
                        .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(imageVector = icon, contentDescription = null)
                Text(text = text)
                Spacer(modifier = Modifier.weight(1.0f))
                Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colorScheme.tertiary
                )
        }
}

@Composable
fun WebLinkLabel(icon: ImageVector, text: String, uri: String) {
        val uriHandler = LocalUriHandler.current
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = colorScheme.background)
                        .clickable {
                                uriHandler.openUri(uri = uri)
                        }
                        .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(imageVector = icon, contentDescription = null)
                Text(text = text)
                Spacer(modifier = Modifier.weight(1.0f))
                Icon(
                        imageVector = Icons.Outlined.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colorScheme.tertiary
                )
        }
}

@Composable
fun OpenAppLabel(icon: ImageVector, text: String, uri: String) {
        val uriHandler = LocalUriHandler.current
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = colorScheme.background)
                        .clickable {
                                uriHandler.openUri(uri = uri)
                        }
                        .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(imageVector = icon, contentDescription = null)
                Text(text = text)
                Spacer(modifier = Modifier.weight(1.0f))
                Icon(
                        imageVector = Icons.Outlined.ArrowOutward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colorScheme.tertiary
                )
        }
}
