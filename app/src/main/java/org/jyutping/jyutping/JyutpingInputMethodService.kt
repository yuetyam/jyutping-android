package org.jyutping.jyutping

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import org.jyutping.jyutping.extensions.empty
import org.jyutping.jyutping.extensions.isReverseLookupTrigger
import org.jyutping.jyutping.extensions.separator
import org.jyutping.jyutping.extensions.separatorChar
import org.jyutping.jyutping.extensions.space
import org.jyutping.jyutping.extensions.toneConverted
import org.jyutping.jyutping.keyboard.Candidate
import org.jyutping.jyutping.keyboard.Cangjie
import org.jyutping.jyutping.keyboard.CangjieVariant
import org.jyutping.jyutping.keyboard.Engine
import org.jyutping.jyutping.keyboard.InputMethodMode
import org.jyutping.jyutping.keyboard.KeyboardCase
import org.jyutping.jyutping.keyboard.KeyboardForm
import org.jyutping.jyutping.keyboard.Pinyin
import org.jyutping.jyutping.keyboard.PinyinSegmentor
import org.jyutping.jyutping.keyboard.QwertyForm
import org.jyutping.jyutping.keyboard.ReturnKeyForm
import org.jyutping.jyutping.keyboard.Segmentor
import org.jyutping.jyutping.keyboard.SpaceKeyForm
import org.jyutping.jyutping.keyboard.Stroke
import org.jyutping.jyutping.keyboard.Structure
import org.jyutping.jyutping.keyboard.length
import org.jyutping.jyutping.keyboard.transformed
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.DatabasePreparer
import org.jyutping.jyutping.utilities.ShapeKeyMap
import org.jyutping.jyutping.utilities.UserLexiconHelper

class JyutpingInputMethodService: LifecycleInputMethodService(),
        ViewModelStoreOwner,
        SavedStateRegistryOwner {

        override fun onCreate() {
                super.onCreate()
                savedStateRegistryController.performRestore(null)
                DatabasePreparer.prepare(this)
        }

        override fun onCreateInputView(): View {
                val isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                if (isDarkMode.value != isNightMode) {
                        isDarkMode.value = isNightMode
                }
                val view = ComposeKeyboardView(this)
                window?.window?.navigationBarColor = if (isDarkMode.value) PresetColor.keyboardDarkBackground.toArgb() else PresetColor.keyboardLightBackground.toArgb()
                window?.window?.decorView?.let { decorView ->
                        decorView.setViewTreeLifecycleOwner(this)
                        decorView.setViewTreeViewModelStoreOwner(this)
                        decorView.setViewTreeSavedStateRegistryOwner(this)
                }
                updateSpaceKeyForm()
                updateReturnKeyForm()
                return view
        }

        override val viewModelStore: ViewModelStore
                get() = store
        override val lifecycle: Lifecycle
                get() = dispatcher.lifecycle

        private val store = ViewModelStore()

        private val savedStateRegistryController = SavedStateRegistryController.create(this)

        override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

        private val sharedPreferences by lazy { getSharedPreferences(UserSettingsKey.PreferencesFileName, Context.MODE_PRIVATE) }

        val isDarkMode: MutableState<Boolean> by lazy {
                val isNightMode: Boolean = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                mutableStateOf(isNightMode)
        }

        val spaceKeyForm: MutableState<SpaceKeyForm> by lazy { mutableStateOf(SpaceKeyForm.Fallback) }
        private fun updateSpaceKeyForm() {
                val newForm: SpaceKeyForm = when {
                        inputMethodMode.value.isABC() -> SpaceKeyForm.English
                        keyboardForm.value == KeyboardForm.TenKeyNumeric -> SpaceKeyForm.Fallback
                        else -> {
                                val isSimplified: Boolean = characterStandard.value.isSimplified()
                                if (isBuffering.value) {
                                        if (candidates.value.isEmpty()) {
                                                if (isSimplified) SpaceKeyForm.ConfirmSimplified else SpaceKeyForm.Confirm
                                        } else {
                                                if (isSimplified) SpaceKeyForm.SelectSimplified else SpaceKeyForm.Select
                                        }
                                } else {
                                        when (keyboardCase.value) {
                                                KeyboardCase.Lowercased -> if (isSimplified) SpaceKeyForm.LowercasedSimplified else SpaceKeyForm.Lowercased
                                                KeyboardCase.Uppercased -> if (isSimplified) SpaceKeyForm.UppercasedSimplified else SpaceKeyForm.Uppercased
                                                KeyboardCase.CapsLocked -> if (isSimplified) SpaceKeyForm.CapsLockedSimplified else SpaceKeyForm.CapsLocked
                                        }
                                }
                        }
                }
                if (spaceKeyForm.value != newForm) {
                        spaceKeyForm.value = newForm
                }
        }

        val returnKeyForm: MutableState<ReturnKeyForm> by lazy { mutableStateOf(ReturnKeyForm.StandbyTraditional) }
        private fun updateReturnKeyForm() {
                val newForm: ReturnKeyForm = when (inputMethodMode.value) {
                        InputMethodMode.ABC -> ReturnKeyForm.StandbyABC
                        InputMethodMode.Cantonese -> {
                                if (isBuffering.value) {
                                        if (characterStandard.value.isSimplified()) ReturnKeyForm.BufferingSimplified else ReturnKeyForm.BufferingTraditional
                                } else {
                                        if (characterStandard.value.isSimplified()) ReturnKeyForm.StandbySimplified else ReturnKeyForm.StandbyTraditional
                                }
                        }
                }
                if (returnKeyForm.value != newForm) {
                        returnKeyForm.value = newForm
                }
        }

        val inputMethodMode: MutableState<InputMethodMode> by lazy { mutableStateOf(InputMethodMode.Cantonese) }
        fun toggleInputMethodMode() {
                val newMode: InputMethodMode = when (inputMethodMode.value) {
                        InputMethodMode.Cantonese -> InputMethodMode.ABC
                        InputMethodMode.ABC -> InputMethodMode.Cantonese
                }
                inputMethodMode.value = newMode
                updateSpaceKeyForm()
                updateReturnKeyForm()
        }

        val qwertyForm: MutableState<QwertyForm> by lazy { mutableStateOf(QwertyForm.Jyutping) }
        private fun updateQwertyForm(form: QwertyForm) {
                if (qwertyForm.value != form) {
                        qwertyForm.value = form
                }
        }

        val keyboardForm: MutableState<KeyboardForm> by lazy { mutableStateOf(KeyboardForm.Alphabetic) }
        fun transformTo(destination: KeyboardForm) {
                if (isBuffering.value) {
                        val shouldKeepBuffer: Boolean = (keyboardForm.value == KeyboardForm.Alphabetic) || (keyboardForm.value == KeyboardForm.CandidateBoard)
                        if (!shouldKeepBuffer) {
                                bufferText = String.empty
                        }
                }
                keyboardForm.value = destination
                adjustKeyboardCase()
                updateSpaceKeyForm()
        }

        val keyboardCase: MutableState<KeyboardCase> by lazy { mutableStateOf(KeyboardCase.Lowercased) }
        private fun updateKeyboardCase(case: KeyboardCase) {
                keyboardCase.value = case
                updateSpaceKeyForm()
        }
        fun shift() {
                val newCase: KeyboardCase = when (keyboardCase.value) {
                        KeyboardCase.Lowercased -> KeyboardCase.Uppercased
                        KeyboardCase.Uppercased -> KeyboardCase.Lowercased
                        KeyboardCase.CapsLocked -> KeyboardCase.Lowercased
                }
                updateKeyboardCase(newCase)
        }
        fun doubleShift() {
                val newCase: KeyboardCase = when (keyboardCase.value) {
                        KeyboardCase.Lowercased -> KeyboardCase.CapsLocked
                        KeyboardCase.Uppercased -> KeyboardCase.CapsLocked
                        KeyboardCase.CapsLocked -> KeyboardCase.Lowercased
                }
                updateKeyboardCase(newCase)
        }
        private fun adjustKeyboardCase() {
                if (keyboardCase.value.isUppercased()) {
                        updateKeyboardCase(KeyboardCase.Lowercased)
                }
        }

        val characterStandard: MutableState<CharacterStandard> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.CharacterStandard, CharacterStandard.Traditional.identifier())
                val standard: CharacterStandard = CharacterStandard.standardOf(savedValue)
                mutableStateOf(standard)
        }
        fun updateCharacterStandard(standard: CharacterStandard) {
                characterStandard.value = standard
                updateSpaceKeyForm()
                updateReturnKeyForm()
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.CharacterStandard, standard.identifier())
                editor.apply()
        }
        val cangjieVariant: MutableState<CangjieVariant> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.CangjieVariant, CangjieVariant.Cangjie5.identifier())
                val variant: CangjieVariant = CangjieVariant.variantOf(savedValue)
                mutableStateOf(variant)
        }
        fun updateCangjieVariant(variant: CangjieVariant) {
                cangjieVariant.value = variant
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.CangjieVariant, variant.identifier())
                editor.apply()
        }

        val isInputMemoryOn: MutableState<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.InputMemory, 1)
                val isOn: Boolean = (savedValue == 1)
                mutableStateOf(isOn)
        }
        fun updateInputMemoryState(isOn: Boolean) {
                isInputMemoryOn.value = isOn
                val value2save: Int = if (isOn) 1 else 2
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.InputMemory, value2save)
                editor.apply()
        }

        private val selectedCandidates: MutableList<Candidate> by lazy { mutableListOf() }
        private val userDB by lazy { UserLexiconHelper(this) }
        fun clearUserLexicon() {
                userDB.deleteAll()
        }

        val candidateState: MutableIntState by lazy { mutableIntStateOf(1) }
        val candidates: MutableState<List<Candidate>> by lazy { mutableStateOf(listOf()) }
        private val db by lazy { DatabaseHelper(this, DatabasePreparer.databaseName) }
        private var bufferText: String = String.empty
                set(value) {
                        candidates.value = emptyList()
                        field = value
                        when (value.firstOrNull()) {
                                null -> {
                                        currentInputConnection.setComposingText(value, value.length)
                                        currentInputConnection.finishComposingText()
                                        if (isBuffering.value) {
                                                isBuffering.value = false
                                                if (isInputMemoryOn.value && selectedCandidates.isNotEmpty()) {
                                                        userDB.process(selectedCandidates)
                                                }
                                                selectedCandidates.clear()
                                        }
                                        if (keyboardForm.value == KeyboardForm.CandidateBoard) {
                                                transformTo(KeyboardForm.Alphabetic)
                                        }
                                        updateQwertyForm(QwertyForm.Jyutping)
                                }
                                'r' -> {
                                        if (value.length < 2) {
                                                currentInputConnection.setComposingText(value, value.length)
                                        } else {
                                                val text = value.drop(1)
                                                val segmentation = PinyinSegmentor.segment(text, db)
                                                val suggestions = Pinyin.reverseLookup(text, segmentation, db)
                                                val tailMark: String = run {
                                                        val firstCandidate = suggestions.firstOrNull()
                                                        if (firstCandidate != null && firstCandidate.input.length == text.length) {
                                                                firstCandidate.mark
                                                        } else {
                                                                val bestScheme = segmentation.firstOrNull()
                                                                val leadingLength: Int = bestScheme?.map { it.length }?.fold(0) { acc, i -> acc + i } ?: 0
                                                                val leadingText: String = bestScheme?.joinToString(separator = String.space) ?: String.empty
                                                                when (leadingLength) {
                                                                        0 -> text
                                                                        text.length -> leadingText
                                                                        else -> (leadingText + String.space + text.drop(leadingLength))
                                                                }
                                                        }
                                                }
                                                val mark = "r $tailMark"
                                                currentInputConnection.setComposingText(mark, mark.length)
                                                candidates.value = suggestions.map { it.transformed(characterStandard.value, db) }.distinct()
                                        }
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                                'v' -> {
                                        updateQwertyForm(QwertyForm.Cangjie)
                                        if (value.length < 2) {
                                                currentInputConnection.setComposingText(value, value.length)
                                        } else {
                                                val text = value.drop(1)
                                                val converted = text.mapNotNull { ShapeKeyMap.cangjieCode(it) }
                                                val isValidSequence: Boolean = converted.isNotEmpty() && (converted.size == text.length)
                                                if (isValidSequence) {
                                                        val mark = converted.joinToString(separator = PresetString.EMPTY)
                                                        currentInputConnection.setComposingText(mark, mark.length)
                                                        val suggestions = Cangjie.reverseLookup(text, cangjieVariant.value, db)
                                                        candidates.value = suggestions.map { it.transformed(characterStandard.value, db) }.distinct()
                                                } else {
                                                        currentInputConnection.setComposingText(bufferText, bufferText.length)
                                                        candidates.value = emptyList()
                                                }
                                        }
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                                'x' -> {
                                        updateQwertyForm(QwertyForm.Stroke)
                                        if (value.length < 2) {
                                                currentInputConnection.setComposingText(value, value.length)
                                        } else {
                                                val text = value.drop(1)
                                                val transformed = ShapeKeyMap.strokeTransform(text)
                                                val converted = transformed.mapNotNull { ShapeKeyMap.strokeCode(it) }
                                                val isValidSequence: Boolean = converted.isNotEmpty() && (converted.size == text.length)
                                                if (isValidSequence) {
                                                        val mark = converted.joinToString(separator = PresetString.EMPTY)
                                                        currentInputConnection.setComposingText(mark, mark.length)
                                                        val suggestions = Stroke.reverseLookup(transformed, db)
                                                        candidates.value = suggestions.map { it.transformed(characterStandard.value, db) }.distinct()
                                                } else {
                                                        currentInputConnection.setComposingText(bufferText, bufferText.length)
                                                        candidates.value = emptyList()
                                                }
                                        }
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                                'q' -> {
                                        if (value.length < 2) {
                                                currentInputConnection.setComposingText(value, value.length)
                                        } else {
                                                val text = value.drop(1)
                                                val segmentation = Segmentor.segment(text, db)
                                                val tailMark: String = run {
                                                        val bestScheme = segmentation.firstOrNull()
                                                        val leadingLength: Int = bestScheme?.length() ?: 0
                                                        val leadingText: String = bestScheme?.joinToString(separator = String.space) { it.text } ?: String.empty
                                                        when (leadingLength) {
                                                                0 -> text
                                                                text.length -> leadingText
                                                                else -> (leadingText + String.space + text.drop(leadingLength))
                                                        }
                                                }
                                                val mark = "q $tailMark"
                                                currentInputConnection.setComposingText(mark, mark.length)
                                                val suggestions = Structure.reverseLookup(text, segmentation, db)
                                                candidates.value = suggestions.map { it.transformed(characterStandard.value, db) }.distinct()
                                        }
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                                else -> {
                                        val processingText: String = value.toneConverted()
                                        val segmentation = Segmentor.segment(processingText, db)
                                        val userLexiconSuggestions: List<Candidate> = if (isInputMemoryOn.value) userDB.suggest(text = processingText, segmentation = segmentation) else emptyList()
                                        val suggestions = Engine.suggest(text = processingText, segmentation = segmentation, db = db)
                                        val mark: String = run {
                                                val userLexiconMark = userLexiconSuggestions.firstOrNull()?.mark
                                                if (userLexiconMark != null) {
                                                        userLexiconMark
                                                } else {
                                                        val firstCandidate = suggestions.firstOrNull()
                                                        if (firstCandidate != null && firstCandidate.input.length == processingText.length) firstCandidate.mark else processingText
                                                }
                                        }
                                        currentInputConnection.setComposingText(mark, mark.length)
                                        candidates.value = (userLexiconSuggestions + suggestions).map { it.transformed(characterStandard.value, db) }.distinct()
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                        }
                        candidateState.intValue += 1
                        updateSpaceKeyForm()
                        updateReturnKeyForm()
                }
        val isBuffering: MutableState<Boolean> by lazy { mutableStateOf(false) }
        fun clearBuffer() {
                bufferText = String.empty
        }
        fun process(text: String) {
                when (inputMethodMode.value) {
                        InputMethodMode.Cantonese -> {
                                bufferText += text
                        }
                        InputMethodMode.ABC -> {
                                currentInputConnection.commitText(text, text.length)
                        }
                }
                adjustKeyboardCase()
        }
        fun input(text: String) {
                currentInputConnection.commitText(text, text.length)
                adjustKeyboardCase()
        }
        fun select(candidate: Candidate) {
                currentInputConnection.commitText(candidate.text, candidate.text.length)
                selectedCandidates.add(candidate)
                val firstChar = bufferText.firstOrNull()
                when {
                        (firstChar == null) -> {}
                        firstChar.isReverseLookupTrigger() -> {
                                val inputLength = candidate.input.length
                                val leadingLength = inputLength + 1
                                if (bufferText.length > leadingLength) {
                                        val tail = bufferText.drop(leadingLength)
                                        bufferText = "${firstChar}${tail}"
                                } else {
                                        bufferText = String.empty
                                }
                        }
                        else -> {
                                val inputLength: Int = candidate.input.replace(Regex("([456])"), "RR").length
                                var tail = bufferText.drop(inputLength)
                                while (tail.startsWith(Char.separatorChar)) {
                                        tail = tail.drop(1)
                                }
                                bufferText = tail
                        }
                }
        }
        fun backspace() {
                if (isBuffering.value) {
                        bufferText = bufferText.dropLast(1)
                } else {
                        currentInputConnection.deleteSurroundingText(1, 0)
                }
        }
        fun performReturn() {
                if (isBuffering.value) {
                        currentInputConnection.commitText(bufferText, bufferText.length)
                        bufferText = String.empty
                } else {
                        sendDefaultEditorAction(true)
                }
        }
        fun space() {
                if (isBuffering.value) {
                        val firstCandidate = candidates.value.firstOrNull()
                        if (firstCandidate == null) {
                                currentInputConnection.commitText(bufferText, bufferText.length)
                                bufferText = String.empty
                        } else {
                                select(firstCandidate)
                        }
                } else {
                        currentInputConnection.commitText(String.space, String.space.length)
                }
        }
        fun dismissKeyboard() {
                requestHideSelf(InputMethodManager.HIDE_NOT_ALWAYS)
        }
        fun leftKey() {
                if (isBuffering.value) {
                        bufferText += String.separator
                } else {
                        val text: String = when (inputMethodMode.value) {
                                InputMethodMode.Cantonese -> "，"
                                InputMethodMode.ABC -> ","
                        }
                        currentInputConnection.commitText(text, text.length)
                }
        }
        fun rightKey() {
                if (isBuffering.value) {
                        bufferText += String.separator
                } else {
                        val text: String = when (inputMethodMode.value) {
                                InputMethodMode.Cantonese -> "。"
                                InputMethodMode.ABC -> "."
                        }
                        currentInputConnection.commitText(text, text.length)
                }
        }
}
