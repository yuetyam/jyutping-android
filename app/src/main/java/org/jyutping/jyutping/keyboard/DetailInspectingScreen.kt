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
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.extensions.characterCount
import org.jyutping.jyutping.extensions.codePointsText
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetString
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

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
                                        if (candidate.isNotCantonese) {
                                                Text(
                                                        text = candidate.text,
                                                        color = if (isDarkMode) Color.White else Color.Black,
                                                        style = MaterialTheme.typography.headlineSmall
                                                )
                                        } else {
                                                Row(
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                        verticalAlignment = Alignment.Bottom
                                                ) {
                                                        RubyStackRow(text = candidate.lexicon.text, romanization = candidate.lexicon.romanization, isDarkMode = isDarkMode)
                                                        if (candidate.text != candidate.lexicon.text) {
                                                                Text(
                                                                        text = candidate.text,
                                                                        color = if (isDarkMode) Color.White else Color.Black,
                                                                        modifier = Modifier.alpha(0.66f)
                                                                )
                                                        }
                                                }
                                        }
                                        if (candidate.lexicon.text.characterCount == 1) {
                                                Text(
                                                        text = "Unicode: ${candidate.lexicon.text.codePointsText}",
                                                        color = if (isDarkMode) Color.White else Color.Black,
                                                        style = MaterialTheme.typography.bodySmall
                                                )
                                        }
                                        if (candidate.isCantonese) {
                                                Text(
                                                        text = stringResource(id = R.string.detail_inspecting_summary_input_count) + ": ${inspectedMemory.first}",
                                                        color = if (isDarkMode) Color.White else Color.Black,
                                                        style = MaterialTheme.typography.bodySmall
                                                )
                                        }
                                        if (inspectedMemory.first > 0L) {
                                                Text(
                                                        text = stringResource(id = R.string.detail_inspecting_summary_latest) + ": ${inspectedMemory.second.toDisplayDate()}",
                                                        color = if (isDarkMode) Color.White else Color.Black,
                                                        style = MaterialTheme.typography.bodySmall
                                                )
                                        }
                                }
                        }
                        if (inspectedMemory.first > 0L) {
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
}

@Composable
private fun RubyStackRow(text: String, romanization: String, isDarkMode: Boolean) {
        val characters: List<String> = text.codePoints().toList().map { code -> buildString { appendCodePoint(code) } }
        val syllables: List<String> = romanization.split(PresetString.SPACE)
        var units: List<DisplayUnit> = emptyList()
        for (index in characters.indices) {
                val character = characters[index]
                syllables.getOrNull(index)?.let { syllable ->
                        val unit = DisplayUnit(character = character, syllable = syllable)
                        units = units + unit
                }
        }
        val annotationLength = syllables.maxBy { it.length }.length
        Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                units.forEach { unit ->
                        CharacterSyllableView(unit = unit, annotationLength = annotationLength, isDarkMode = isDarkMode)
                }
        }
}

private data class DisplayUnit(val character: String, val syllable: String)

@Composable
private fun CharacterSyllableView(unit: DisplayUnit, annotationLength: Int, isDarkMode: Boolean) {
        val annotation: String = run {
                val shortAmount = (annotationLength - unit.syllable.length)
                if (shortAmount <= 0) return@run unit.syllable
                if (shortAmount == 1) return@run PresetString.SPACE + unit.syllable
                val trailing = (shortAmount / 2)
                val leading = (shortAmount - trailing)
                return@run PresetString.SPACE.repeat(leading) + unit.syllable + PresetString.SPACE.repeat(trailing)
        }
        Column(
                modifier = Modifier.width(34.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Text(
                        text = annotation,
                        color = if (isDarkMode) Color.White else Color.Black,
                        autoSize = TextAutoSize.StepBased(minFontSize = 6.sp, maxFontSize = 12.sp),
                        fontSize = 12.sp,
                        overflow = TextOverflow.Visible,
                        maxLines = 1
                )
                Text(
                        text = unit.character,
                        color = if (isDarkMode) Color.White else Color.Black,
                        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 24.sp),
                        fontSize = 24.sp,
                        overflow = TextOverflow.Visible,
                        maxLines = 1
                )
        }
}
