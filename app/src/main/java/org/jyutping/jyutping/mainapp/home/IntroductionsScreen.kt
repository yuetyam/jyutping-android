package org.jyutping.jyutping.mainapp.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.R
import org.jyutping.jyutping.ui.common.TextCard

@Composable
fun IntroductionsScreen() {
        SelectionContainer {
                LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        item {
                                TextCard(
                                        heading = stringResource(id = R.string.introductions_heading_clear_buffer),
                                        content = stringResource(id = R.string.introductions_content_clear_buffer)
                                )
                        }
                        item {
                                TextCard(
                                        heading = stringResource(id = R.string.introductions_heading_position_insertion_point),
                                        content = stringResource(id = R.string.introductions_content_position_insertion_point)
                                )
                        }
                }
        }
}
