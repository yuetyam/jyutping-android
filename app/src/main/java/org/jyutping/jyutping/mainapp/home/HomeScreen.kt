package org.jyutping.jyutping.mainapp.home

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import org.jyutping.jyutping.R
import org.jyutping.jyutping.Screen
import org.jyutping.jyutping.extensions.convertedS2T
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.search.CantoneseLexicon
import org.jyutping.jyutping.search.CantoneseLexiconView
import org.jyutping.jyutping.search.ChoHokView
import org.jyutping.jyutping.search.ChoHokYuetYamCitYiu
import org.jyutping.jyutping.search.FanWanCuetYiu
import org.jyutping.jyutping.search.FanWanView
import org.jyutping.jyutping.search.GwongWanCharacter
import org.jyutping.jyutping.search.GwongWanView
import org.jyutping.jyutping.search.UnihanDefinition
import org.jyutping.jyutping.search.YingWaaFanWan
import org.jyutping.jyutping.search.YingWaaView
import org.jyutping.jyutping.ui.common.NavigationLabel
import org.jyutping.jyutping.ui.common.SearchField
import org.jyutping.jyutping.ui.common.TextCard
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.DatabasePreparer

@Composable
fun HomeScreen(navController: NavHostController) {
        val textState: MutableState<String> = remember { mutableStateOf("") }
        val lexiconState = remember { mutableStateOf<CantoneseLexicon?>(null) }
        val unihanDefinition = remember { mutableStateOf<UnihanDefinition?>(null) }
        val yingWaaEntries = remember { mutableStateOf<List<YingWaaFanWan>>(listOf()) }
        val choHokEntries = remember { mutableStateOf<List<ChoHokYuetYamCitYiu>>(listOf()) }
        val fanWanEntries = remember { mutableStateOf<List<FanWanCuetYiu>>(listOf()) }
        val gwongWanEntries = remember { mutableStateOf<List<GwongWanCharacter>>(listOf()) }
        val helper: DatabaseHelper by lazy { DatabaseHelper(navController.context, DatabasePreparer.DATABASE_NAME) }
        fun searchYingWan(text: String): List<YingWaaFanWan> {
                val char = text.firstOrNull() ?: return emptyList()
                val matched = helper.yingWaaFanWanMatch(char)
                if (matched.isNotEmpty()) return YingWaaFanWan.process(matched)
                val traditionalChar = text.convertedS2T().firstOrNull() ?: char
                val traditionalMatched = helper.yingWaaFanWanMatch(traditionalChar)
                return YingWaaFanWan.process(traditionalMatched)
        }
        fun searchChoHok(text: String): List<ChoHokYuetYamCitYiu> {
                val char = text.firstOrNull() ?: return emptyList()
                val matched = helper.choHokYuetYamCitYiuMatch(char)
                if (matched.isNotEmpty()) return matched
                val traditionalChar = text.convertedS2T().firstOrNull() ?: char
                return helper.choHokYuetYamCitYiuMatch(traditionalChar)
        }
        fun searchFanWan(text: String): List<FanWanCuetYiu> {
                val char = text.firstOrNull() ?: return emptyList()
                val matched = helper.fanWanCuetYiuMatch(char)
                if (matched.isNotEmpty()) return matched
                val traditionalChar = text.convertedS2T().firstOrNull() ?: char
                return helper.fanWanCuetYiuMatch(traditionalChar)
        }
        fun searchGwongWan(text: String): List<GwongWanCharacter> {
                val char = text.firstOrNull() ?: return emptyList()
                val matched = helper.gwongWanMatch(char)
                if (matched.isNotEmpty()) return matched
                val traditionalChar = text.convertedS2T().firstOrNull() ?: char
                return helper.gwongWanMatch(traditionalChar)
        }
        val isKeyboardEnabled: MutableState<Boolean> = remember { mutableStateOf(false) }
        val isKeyboardSelected: MutableState<Boolean> = remember { mutableStateOf(false) }
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_START) {
                                val manager = navController.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                isKeyboardEnabled.value = manager.enabledInputMethodList.any { it.packageName == PresetConstant.keyboardPackageName }
                                isKeyboardSelected.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                        manager.currentInputMethodInfo?.packageName == PresetConstant.keyboardPackageName
                                } else {
                                        val defaultKeyboardId = Settings.Secure.getString(navController.context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
                                        defaultKeyboardId == PresetConstant.keyboardId
                                }
                        }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                }
        }
        SelectionContainer {
                LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        item {
                                SearchField(textState = textState) {
                                        val text = textState.value.trim()
                                        lexiconState.value = helper.searchCantoneseLexicon(text)
                                        val text4definition = lexiconState.value?.text ?: text
                                        unihanDefinition.value = helper.unihanDefinitionMatch(text4definition)
                                        yingWaaEntries.value = searchYingWan(text)
                                        choHokEntries.value = searchChoHok(text)
                                        fanWanEntries.value = searchFanWan(text)
                                        gwongWanEntries.value = searchGwongWan(text)
                                }
                        }
                        lexiconState.value?.let {
                                item {
                                        CantoneseLexiconView(lexicon = it, unihanDefinition = unihanDefinition.value)
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
                        if (gwongWanEntries.value.isNotEmpty()) {
                                item {
                                        GwongWanView(gwongWanEntries.value)
                                }
                        }
                        if (isKeyboardEnabled.value.not()) {
                                item {
                                        DisableSelection {
                                                Button(
                                                        onClick = {
                                                                val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                                                                navController.context.startActivity(intent)
                                                        },
                                                        shape = RoundedCornerShape(8.dp),
                                                        contentPadding = PaddingValues(horizontal = 4.dp)
                                                ) {
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Text(text = stringResource(id = R.string.home_button_enable_keyboard))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                }
                                        }
                                }
                        } else if (isKeyboardSelected.value.not()) {
                                item {
                                        DisableSelection {
                                                Button(
                                                        onClick = {
                                                                (navController.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
                                                        },
                                                        shape = RoundedCornerShape(8.dp),
                                                        contentPadding = PaddingValues(horizontal = 4.dp)
                                                ) {
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Text(text = stringResource(id = R.string.home_button_select_keyboard))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                }
                                        }
                                }
                        }
                        item {
                                TextCard(
                                        heading = stringResource(id = R.string.home_heading_tone_input),
                                        content = stringResource(id = R.string.home_content_tones_input),
                                        subContent = stringResource(id = R.string.home_subcontent_tones_input_examples),
                                        shouldMonospaceContent = true
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
                                        content = stringResource(id = R.string.home_content_cangjie_reverse_lookup),
                                        subContent = stringResource(id = R.string.home_subcontent_cangjie_reverse_lookup_note)
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
                                        shouldMonospaceContent = true
                                )
                        }
                        item {
                                TextCard(
                                        heading = stringResource(id = R.string.home_heading_compose_reverse_lookup),
                                        content = stringResource(id = R.string.home_content_compose_reverse_lookup)
                                )
                        }
                        item {
                                DisableSelection {
                                        Column(
                                                modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(colorScheme.background)
                                                        .fillMaxWidth()
                                        ) {
                                                NavigationLabel(
                                                        icon = Icons.Outlined.Info,
                                                        text = stringResource(id = R.string.home_label_more_introductions)
                                                ) {
                                                        navController.navigate(route = Screen.Introductions.route)
                                                }
                                        }
                                }
                        }
                }
        }
}
