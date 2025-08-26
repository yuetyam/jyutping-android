package org.jyutping.jyutping.mainapp.home

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import org.jyutping.jyutping.R
import org.jyutping.jyutping.Screen
import org.jyutping.jyutping.extensions.isIdeographicChar
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.search.CantoneseLexicon
import org.jyutping.jyutping.search.CantoneseLexiconView
import org.jyutping.jyutping.search.ChoHokView
import org.jyutping.jyutping.search.ChoHokYuetYamCitYiu
import org.jyutping.jyutping.search.FanWanCuetYiu
import org.jyutping.jyutping.search.FanWanView
import org.jyutping.jyutping.search.GwongWanCharacter
import org.jyutping.jyutping.search.GwongWanView
import org.jyutping.jyutping.search.YingWaaFanWan
import org.jyutping.jyutping.search.YingWaaView
import org.jyutping.jyutping.ui.common.NavigationLabel
import org.jyutping.jyutping.ui.common.SearchField
import org.jyutping.jyutping.ui.common.TextCard
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.DatabasePreparer

private typealias YingWaaLexicon = List<YingWaaFanWan>
private typealias ChoHokLexicon = List<ChoHokYuetYamCitYiu>
private typealias FanWanLexicon = List<FanWanCuetYiu>
private typealias GwongWanLexicon = List<GwongWanCharacter>

@Composable
fun HomeScreen(navController: NavHostController) {
        val textState = remember { mutableStateOf(PresetString.EMPTY) }
        val lexicons = remember { mutableStateOf<List<CantoneseLexicon>>(listOf()) }
        val yingWaaLexicons = remember { mutableStateOf<List<YingWaaLexicon>>(listOf()) }
        val choHokLexicons = remember { mutableStateOf<List<ChoHokLexicon>>(listOf()) }
        val fanWanLexicons = remember { mutableStateOf<List<FanWanLexicon>>(listOf()) }
        val gwongWanLexicons = remember { mutableStateOf<List<GwongWanLexicon>>(listOf()) }
        val helper: DatabaseHelper by lazy { DatabaseHelper(navController.context, DatabasePreparer.DATABASE_NAME) }
        fun searchCantoneseLexicons(text: String): List<CantoneseLexicon> {
                val ideographicCharacters = text.mapNotNull { if (it.isIdeographicChar()) it else null }.distinct()
                if (ideographicCharacters.isEmpty()) return listOf(CantoneseLexicon(text))
                val primary = helper.searchCantoneseLexicon(text)
                val shouldReturnEarly: Boolean = text.length < 2 || ideographicCharacters.count() > 3
                if (shouldReturnEarly) return listOf(primary)
                val subLexicons = ideographicCharacters.map { helper.searchCantoneseLexicon(it.toString()) }
                return listOf(primary) + subLexicons
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
                                        if (text.isEmpty()) {
                                                lexicons.value = emptyList()
                                        } else {
                                                lexicons.value = searchCantoneseLexicons(text)
                                        }
                                        val ideographicCharacters = text.mapNotNull { if (it.isIdeographicChar()) it else null }.distinct()
                                        if (ideographicCharacters.isEmpty() || ideographicCharacters.count() > 3) {
                                                yingWaaLexicons.value = emptyList()
                                                choHokLexicons.value = emptyList()
                                                fanWanLexicons.value = emptyList()
                                                gwongWanLexicons.value = emptyList()
                                        } else {
                                                yingWaaLexicons.value = ideographicCharacters.map { helper.searchYingWaaFanWan(it) }.map { YingWaaFanWan.process(it) }.filter { it.isNotEmpty() }
                                                choHokLexicons.value = ideographicCharacters.map { helper.searchChoHokYuetYamCitYiu(it) }.filter { it.isNotEmpty() }
                                                fanWanLexicons.value = ideographicCharacters.map { helper.searchFanWanCuetYiu(it) }.filter { it.isNotEmpty() }
                                                gwongWanLexicons.value = ideographicCharacters.map { helper.searchGwongWan(it) }.filter { it.isNotEmpty() }
                                        }
                                }
                        }
                        items(lexicons.value) { lexicon ->
                                AnimatedContent(targetState = lexicon) {
                                        CantoneseLexiconView(it)
                                }
                        }
                        items(yingWaaLexicons.value) { lexicon ->
                                AnimatedContent(targetState = lexicon) {
                                        YingWaaView(it)
                                }
                        }
                        items(choHokLexicons.value) { lexicon ->
                                AnimatedContent(targetState = lexicon) {
                                        ChoHokView(it)
                                }
                        }
                        items(fanWanLexicons.value) { lexicon ->
                                AnimatedContent(targetState = lexicon) {
                                        FanWanView(it)
                                }
                        }
                        items(gwongWanLexicons.value) { lexicon ->
                                AnimatedContent(targetState = lexicon) {
                                        GwongWanView(it)
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
                                                        shape = CircleShape
                                                ) {
                                                        Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterHorizontally),
                                                                verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                                Icon(imageVector = Icons.Outlined.Settings, contentDescription = null)
                                                                Text(text = stringResource(id = R.string.home_button_enable_keyboard))
                                                                Icon(imageVector = Icons.Outlined.Settings, contentDescription = null, modifier = Modifier.alpha(0f))
                                                        }
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
                                                        shape = CircleShape
                                                ) {
                                                        Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterHorizontally),
                                                                verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                                Icon(imageVector = Icons.Outlined.Keyboard, contentDescription = null)
                                                                Text(text = stringResource(id = R.string.home_button_select_keyboard))
                                                                Icon(imageVector = Icons.Outlined.Keyboard, contentDescription = null, modifier = Modifier.alpha(0f))
                                                        }
                                                }
                                        }
                                }
                        }
                        item {
                                TextCard(
                                        heading = stringResource(id = R.string.home_heading_tone_input),
                                        content = stringResource(id = R.string.home_content_tone_input),
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
                                                        .clip(RoundedCornerShape(10.dp))
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
