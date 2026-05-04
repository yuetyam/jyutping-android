package org.jyutping.jyutping.app.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.AppleColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.ui.common.TextCard

@Composable
fun DisplayLanguagesScreen() {
        val context = LocalContext.current
        SelectionContainer {
                LazyColumn(
                        contentPadding = PaddingValues(start = 14.dp, top = 16.dp, end = 14.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        item {
                                TextCard(
                                        indicator = "A",
                                        indicatorColor = AppleColor.green,
                                        heading = stringResource(id = R.string.display_languages_heading),
                                        content = stringResource(id = R.string.display_languages_content),
                                        subContent = stringResource(id = R.string.display_languages_sub_content),
                                )
                        }
                        item {
                                DisableSelection {
                                        Button(
                                                onClick = {
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                                val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
                                                                        .apply { data = Uri.fromParts("package", context.packageName, null) }
                                                                        .apply { setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                                                                context.startActivity(intent)
                                                        }
                                                },
                                                shape = CircleShape,
                                                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.background, contentColor = PresetColor.blue),
                                                contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
                                        ) {
                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterHorizontally),
                                                        verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                        Icon(
                                                                imageVector = ImageVector.vectorResource(id = R.drawable.button_settings),
                                                                contentDescription = null,
                                                                modifier = Modifier.size(20.dp)
                                                        )
                                                        Text(
                                                                text = stringResource(id = R.string.general_go_to_settings),
                                                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                                                fontWeight = FontWeight.Medium
                                                        )
                                                        Icon(
                                                                imageVector = ImageVector.vectorResource(id = R.drawable.button_settings),
                                                                contentDescription = null,
                                                                modifier = Modifier.size(20.dp).alpha(0f)
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}
