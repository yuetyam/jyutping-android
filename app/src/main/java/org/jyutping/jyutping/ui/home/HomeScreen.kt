package org.jyutping.jyutping.ui.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jyutping.jyutping.CantoneseLexicon
import org.jyutping.jyutping.DatabaseHelper
import org.jyutping.jyutping.DatabasePreparer
import org.jyutping.jyutping.R
import org.jyutping.jyutping.Screen
import org.jyutping.jyutping.ui.common.NavigationLabel
import org.jyutping.jyutping.ui.common.SearchField
import org.jyutping.jyutping.ui.common.TextCard

@Composable
fun HomeScreen(navController: NavHostController) {
        val lexiconState = remember { mutableStateOf<CantoneseLexicon?>(null) }
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                item {
                        SearchField(onSubmit = {
                                val helper = DatabaseHelper(navController.context, DatabasePreparer.DATABASE_NAME)
                                lexiconState.value = helper.search(it)
                        })
                }
                lexiconState.value?.let { lexicon ->
                        item {
                                Row {
                                        Text(text = "文字")
                                        Text(text = ": ")
                                        Text(text = lexicon.text)
                                }
                        }
                        items(lexicon.pronunciations) {
                                Text(text = it.romanization)
                        }
                }
                item {
                        TextCard(
                                heading = stringResource(id = R.string.home_heading_tones_input),
                                content = stringResource(id = R.string.home_content_tones_input),
                                subContent = stringResource(id = R.string.home_subcontent_tones_input_examples),
                                shouldContentMonospaced = true
                        )
                }
                item {
                        TextCard(
                                heading = stringResource(id = R.string.home_heading_pinyin_reverse_lookup),
                                content = stringResource(id = R.string.home_content_pinyin_reverse_lookup)
                        )
                }
                item {
                        TextCard(
                                heading = stringResource(id = R.string.home_heading_cangjie_reverse_lookup),
                                content = stringResource(id = R.string.home_content_cangjie_reverse_lookup)
                        )
                }
                item {
                        TextCard(
                                heading = stringResource(id = R.string.home_heading_stroke_reverse_lookup),
                                content = stringResource(id = R.string.home_content_stroke_reverse_lookup)
                        )
                        TextCard(
                                heading = stringResource(id = R.string.home_heading_stroke_code),
                                content = "w = 橫(waang)\ns = 豎(syu)\na = 撇\nd = 點(dim)\nz = 折(zit)",
                                shouldContentMonospaced = true
                        )
                }
                item {
                        TextCard(
                                heading = stringResource(id = R.string.home_heading_compose_reverse_lookup),
                                content = stringResource(id = R.string.home_content_compose_reverse_lookup)
                        )
                }
                item {
                        NavigationLabel(icon = Icons.Outlined.Info, text = stringResource(id = R.string.home_label_more_introductions)) {
                                navController.navigate(route = Screen.Introductions.route)
                        }
                }
        }
}
