package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.extensions.characterCount
import org.jyutping.jyutping.extensions.codePointsText
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DetailInspectingScreen(height: Dp) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val candidate by context.inspectingCandidate.collectAsState()
        val inspectedMemory by context.inspectedMemory.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val tintColor: Color = if (isDarkMode) Color.White else Color.Black
        val backColor: Color = if (isDarkMode) Color.Black else Color.White
        val sectionShape = RoundedCornerShape(16.dp)

        fun Long.toDisplayDate(): String = Instant.ofEpochMilli(this@toDisplayDate)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        Column(
                modifier = Modifier
                        .background(
                                if (isHighContrastPreferred) {
                                        if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
                                } else {
                                        if (isDarkMode) PresetColor.darkBackground else PresetColor.lightBackground
                                }
                        )
                        .systemBarsPadding()
                        .padding(bottom = extraBottomPadding.applyingValue.dp)
                        .height(height)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Row(
                        modifier = Modifier
                                .height(44.dp)
                                .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        AdvancedIconButton(
                                icon = Icons.Rounded.ArrowUpward
                        ) {
                                context.audioFeedback(SoundEffect.Back)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.transformTo(KeyboardForm.Primary)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                                text = stringResource(id = R.string.keyboard_settings_navigation_hint),
                                modifier = Modifier.alpha(0f),
                                color = tintColor
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        AdvancedIconButton(
                                modifier = Modifier.alpha(0f),
                                icon = ImageVector.vectorResource(id = R.drawable.button_expand),
                                iconSize = 20.dp
                        ) {
                                context.audioFeedback(SoundEffect.Click)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                }
                LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        item {
                                Column(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = backColor, shape = sectionShape)
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                        Column(
                                                modifier = Modifier.fillMaxWidth()
                                        ) {
                                                Text(
                                                        text = candidate.lexicon.romanization,
                                                        style = MaterialTheme.typography.bodySmall
                                                )
                                                if (candidate.text == candidate.lexicon.text) {
                                                        Text(
                                                                text = candidate.lexicon.text,
                                                                style = MaterialTheme.typography.headlineSmall
                                                        )
                                                } else {
                                                        Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                                Text(
                                                                        text = candidate.lexicon.text,
                                                                        style = MaterialTheme.typography.headlineSmall
                                                                )
                                                                Text(
                                                                        text = candidate.text,
                                                                        modifier = Modifier.alpha(0.66f)
                                                                )
                                                        }
                                                }
                                        }
                                        if (candidate.lexicon.text.characterCount == 1) {
                                                Text(
                                                        text = "Unicode: ${candidate.lexicon.text.codePointsText}",
                                                        style = MaterialTheme.typography.bodySmall
                                                )
                                        }
                                        Text(
                                                text = stringResource(id = R.string.detail_inspecting_summary_input_count) + ": ${inspectedMemory.first}",
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                        if (inspectedMemory.first > 0L) {
                                                Text(
                                                        text = stringResource(id = R.string.detail_inspecting_summary_latest) + ": ${inspectedMemory.second.toDisplayDate()}",
                                                        style = MaterialTheme.typography.bodySmall
                                                )
                                        }
                                }
                        }
                        item {
                                TextButton(
                                        onClick = {
                                                context.forgetCandidate(candidate)
                                                context.inspect(candidate)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = CircleShape,
                                        colors = ButtonDefaults.textButtonColors(containerColor = backColor, contentColor = Color.Red)
                                ) {
                                        Text(
                                                text = stringResource(id = R.string.detail_inspecting_forget_candidate_button_title),
                                                textAlign = TextAlign.Center
                                        )
                                }
                        }
                }
        }
}
