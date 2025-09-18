package org.jyutping.jyutping.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.extensions.characterCount
import org.jyutping.jyutping.extensions.codePointText
import org.jyutping.jyutping.speech.Speaker
import org.jyutping.jyutping.ui.common.SeparatorMark

@Composable
fun WordTextLabel(word: String) {
        Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Row(
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = "文字",
                                color = colorScheme.onBackground
                        )
                        SeparatorMark()
                        Text(
                                text = word,
                                color = colorScheme.onBackground
                        )
                }
                if (word.characterCount() == 1) {
                        Text(
                                text = word.codePointText(),
                                modifier = Modifier.alpha(0.75f),
                                color = colorScheme.onBackground,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                        )
                }
                Spacer(modifier = Modifier.weight(1f))
                Speaker(cantonese = word)
        }
}
