package org.jyutping.jyutping.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jyutping.jyutping.search.CantoneseLexicon
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.DatabasePreparer
import org.jyutping.jyutping.R
import org.jyutping.jyutping.Screen
import org.jyutping.jyutping.extensions.convertedS2T
import org.jyutping.jyutping.search.CantoneseLexiconView
import org.jyutping.jyutping.search.ChoHokView
import org.jyutping.jyutping.search.ChoHokYuetYamCitYiu
import org.jyutping.jyutping.search.FanWanCuetYiu
import org.jyutping.jyutping.search.FanWanView
import org.jyutping.jyutping.search.YingWaaFanWan
import org.jyutping.jyutping.search.YingWaaView
import org.jyutping.jyutping.ui.common.NavigationLabel
import org.jyutping.jyutping.ui.common.SearchField
import org.jyutping.jyutping.ui.common.TextCard

@Composable
fun HomeScreen(navController: NavHostController) {
        val lexiconState = remember { mutableStateOf<CantoneseLexicon?>(null) }
        val yingWaaEntries = remember { mutableStateOf<List<YingWaaFanWan>>(listOf()) }
        val choHokEntries = remember { mutableStateOf<List<ChoHokYuetYamCitYiu>>(listOf()) }
        val fanWanEntries = remember { mutableStateOf<List<FanWanCuetYiu>>(listOf()) }
        val helper: DatabaseHelper by lazy { DatabaseHelper(navController.context, DatabasePreparer.DATABASE_NAME) }
        fun searchYingWan(text: String): List<YingWaaFanWan> {
                if (text.isBlank()) return listOf()
                val char = text.first()
                val matched = helper.matchYingWaaFanWan(char)
                if (matched.isNotEmpty()) return YingWaaFanWan.process(matched)
                val traditionalChar = text.convertedS2T().firstOrNull() ?: char
                val traditionalMatched = helper.matchYingWaaFanWan(traditionalChar)
                return YingWaaFanWan.process(traditionalMatched)
        }
        fun searchChoHok(text: String): List<ChoHokYuetYamCitYiu> {
                if (text.isBlank()) return listOf()
                val char = text.first()
                val matched = helper.matchChoHokYuetYamCitYiu(char)
                if (matched.isNotEmpty()) return matched
                val traditionalChar = text.convertedS2T().firstOrNull() ?: char
                return helper.matchChoHokYuetYamCitYiu(traditionalChar)
        }
        fun searchFanWan(text: String): List<FanWanCuetYiu> {
                if (text.isBlank()) return listOf()
                val char = text.first()
                val matched = helper.matchFanWanCuetYiu(char)
                if (matched.isNotEmpty()) return matched
                val traditionalChar = text.convertedS2T().firstOrNull() ?: char
                return helper.matchFanWanCuetYiu(traditionalChar)
        }
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                item {
                        SearchField {
                                lexiconState.value = helper.search(it)
                                yingWaaEntries.value = searchYingWan(it)
                                choHokEntries.value = searchChoHok(it)
                                fanWanEntries.value = searchFanWan(it)
                        }
                }
                lexiconState.value?.let {
                        item {
                                CantoneseLexiconView(it)
                        }
                }
                if (yingWaaEntries.value.isNotEmpty()) {
                        item {
                                YingWaaView(yingWaaEntries.value)
                        }
                }
                if (choHokEntries.value.isNotEmpty()) {
                        item {
                                ChoHokView(choHokEntries.value)
                        }
                }
                if (fanWanEntries.value.isNotEmpty()) {
                        item {
                                FanWanView(fanWanEntries.value)
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
                }
                item {
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
