package org.jyutping.jyutping.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.ui.common.SeparatorMark

@Composable
fun CantoneseLexiconView(lexicon: CantoneseLexicon, unihanDefinition: UnihanDefinition?) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorScheme.background, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                WordTextLabel(lexicon.text)
                lexicon.pronunciations.map {
                        Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                                HorizontalDivider()
                                PronunciationLabel(it)
                        }
                }
                unihanDefinition?.let {
                        Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                                HorizontalDivider()
                                Row {
                                        Text(
                                                text = "英文",
                                                color = colorScheme.onBackground
                                        )
                                        SeparatorMark()
                                        Text(
                                                text = it.definition,
                                                color = colorScheme.onBackground
                                        )
                                }
                        }
                }
        }
}
