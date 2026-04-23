package org.jyutping.jyutping.app.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun TextToSpeechScreen() {
        SelectionContainer {
                LazyColumn(
                        contentPadding = PaddingValues(start = 14.dp, top = 8.dp, end = 14.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        item {
                                Text(
                                        text = stringResource(id = R.string.tts_notice_row1),
                                        color = colorScheme.onBackground,
                                        modifier = Modifier.fillMaxWidth()
                                                .background(
                                                        color = colorScheme.background,
                                                        shape = RoundedCornerShape(12.dp)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                )
                        }
                        item {
                                Text(
                                        text = stringResource(id = R.string.tts_notice_row2),
                                        color = colorScheme.onBackground,
                                        modifier = Modifier.fillMaxWidth()
                                                .background(
                                                        color = colorScheme.background,
                                                        shape = RoundedCornerShape(12.dp)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                )
                        }
                        item {
                                Column(
                                        modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(colorScheme.background)
                                                .fillMaxWidth()
                                ) {
                                        Text(
                                                text = stringResource(id = R.string.tts_notice_row3),
                                                color = colorScheme.onBackground,
                                                modifier = Modifier.fillMaxWidth()
                                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                        )
                                        HorizontalDivider()
                                        TTSLinkButton()
                                }
                        }
                }
        }
}

@Composable
private fun TTSLinkButton() {
        val uriHandler = LocalUriHandler.current
        val webAddress = "https://jyutping.app/android/tts"
        Button(
                onClick = {
                        uriHandler.openUri(webAddress)
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = LocalContentColor.current
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = webAddress,
                                color = PresetColor.blue,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.weight(1.0f))
                        Icon(
                                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp).alpha(0.66f),
                                tint = colorScheme.onBackground
                        )
                }
        }
}
