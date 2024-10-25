package org.jyutping.jyutping

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
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
import org.jyutping.jyutping.extensions.convertedS2T
import org.jyutping.jyutping.extensions.convertedT2S
import org.jyutping.jyutping.extensions.formattedForMark
import org.jyutping.jyutping.extensions.isReverseLookupTrigger
import org.jyutping.jyutping.extensions.isSeparatorOrTone
import org.jyutping.jyutping.extensions.toneConverted
import org.jyutping.jyutping.keyboard.Candidate
import org.jyutping.jyutping.keyboard.Cangjie
import org.jyutping.jyutping.keyboard.CangjieVariant
import org.jyutping.jyutping.keyboard.CommentStyle
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
import org.jyutping.jyutping.presets.PresetCharacter
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
                window?.window?.decorView?.let { decorView ->
                        decorView.setViewTreeLifecycleOwner(this)
                        decorView.setViewTreeViewModelStoreOwner(this)
                        decorView.setViewTreeSavedStateRegistryOwner(this)
                }
                return ComposeKeyboardView(this)
        }
        override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
                super.onStartInput(attribute, restarting)
                val isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                window?.window?.navigationBarColor = if (isNightMode) PresetColor.keyboardDarkBackground.toArgb() else PresetColor.keyboardLightBackground.toArgb()
                isDarkMode.value = isNightMode
                inputMethodMode.value = InputMethodMode.Cantonese
                keyboardForm.value = KeyboardForm.Alphabetic
                qwertyForm.value = QwertyForm.Jyutping
                updateSpaceKeyForm()
                updateReturnKeyForm()
        }
        override fun onFinishInputView(finishingInput: Boolean) {
                if (selectedCandidates.isNotEmpty()) {
                        selectedCandidates.clear()
                }
                if (isBuffering.value) {
                        currentInputConnection.commitText(bufferText, bufferText.length)
                        bufferText = PresetString.EMPTY
                }
                if (candidates.value.isNotEmpty()) {
                        candidates.value = emptyList()
                }
                super.onFinishInputView(finishingInput)
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
                        if (shouldKeepBuffer.not()) {
                                bufferText = PresetString.EMPTY
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
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.CharacterStandard, CharacterStandard.Traditional.identifier)
                val standard: CharacterStandard = CharacterStandard.standardOf(savedValue)
                mutableStateOf(standard)
        }
        fun updateCharacterStandard(standard: CharacterStandard) {
                characterStandard.value = standard
                updateSpaceKeyForm()
                updateReturnKeyForm()
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.CharacterStandard, standard.identifier)
                editor.apply()
        }
        val isAudioFeedbackOn: MutableState<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.AudioFeedback, 101)
                val isOn: Boolean = (savedValue == 101)
                mutableStateOf(isOn)
        }
        fun updateAudioFeedback(isOn: Boolean) {
                isAudioFeedbackOn.value = isOn
                val value: Int = if (isOn) 101 else 102
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.AudioFeedback, value)
                editor.apply()
        }
        val isHapticFeedbackOn: MutableState<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.HapticFeedback, 101)
                val isOn: Boolean = (savedValue == 101)
                mutableStateOf(isOn)
        }
        fun updateHapticFeedback(isOn: Boolean) {
                isHapticFeedbackOn.value = isOn
                val value: Int = if (isOn) 101 else 102
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.HapticFeedback, value)
                editor.apply()
        }
        val showLowercaseKeys: MutableState<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.KeyCase, 1)
                val isLowercase: Boolean = (savedValue == 1)
                mutableStateOf(isLowercase)
        }
        fun updateShowLowercaseKeys(isOn: Boolean) {
                showLowercaseKeys.value = isOn
                val value2save: Int = if (isOn) 1 else 2
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.KeyCase, value2save)
                editor.apply()
        }
        val needsInputModeSwitchKey: MutableState<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.GlobeKey, 0)
                val needs: Boolean = (savedValue == 1)
                mutableStateOf(needs)
        }
        fun updateNeedsInputModeSwitchKey(needs: Boolean) {
                needsInputModeSwitchKey.value = needs
                val value2save: Int = if (needs) 1 else 2
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.GlobeKey, value2save)
                editor.apply()
        }
        val needsLeftKey: MutableState<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.LeftKey, 1)
                val needs: Boolean = (savedValue == 1)
                mutableStateOf(needs)
        }
        fun updateNeedsLeftKey(needs: Boolean) {
                needsLeftKey.value = needs
                val value2save: Int = if (needs) 1 else 2
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.LeftKey, value2save)
                editor.apply()
        }
        val needsRightKey: MutableState<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.RightKey, 1)
                val needs: Boolean = (savedValue == 1)
                mutableStateOf(needs)
        }
        fun updateNeedsRightKey(needs: Boolean) {
                needsRightKey.value = needs
                val value2save: Int = if (needs) 1 else 2
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.RightKey, value2save)
                editor.apply()
        }
        val isEmojiSuggestionsOn: MutableState<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.Emoji, 1)
                val isOn: Boolean = (savedValue == 1)
                mutableStateOf(isOn)
        }
        fun updateEmojiSuggestionsState(isOn: Boolean) {
                isEmojiSuggestionsOn.value = isOn
                val value2save: Int = if (isOn) 1 else 2
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.Emoji, value2save)
                editor.apply()
        }
        val commentStyle: MutableState<CommentStyle> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.CommentStyle, CommentStyle.AboveCandidates.identifier)
                val style: CommentStyle = CommentStyle.styleOf(savedValue)
                mutableStateOf(style)
        }
        fun updateCommentStyle(style: CommentStyle) {
                commentStyle.value = style
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.CommentStyle, style.identifier)
                editor.apply()
        }
        val cangjieVariant: MutableState<CangjieVariant> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.CangjieVariant, CangjieVariant.Cangjie5.identifier)
                val variant: CangjieVariant = CangjieVariant.variantOf(savedValue)
                mutableStateOf(variant)
        }
        fun updateCangjieVariant(variant: CangjieVariant) {
                cangjieVariant.value = variant
                val editor = sharedPreferences.edit()
                editor.putInt(UserSettingsKey.CangjieVariant, variant.identifier)
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
        private var bufferText: String = PresetString.EMPTY
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
                                                                val leadingText: String = bestScheme?.joinToString(separator = PresetString.SPACE) ?: PresetString.EMPTY
                                                                when (leadingLength) {
                                                                        0 -> text
                                                                        text.length -> leadingText
                                                                        else -> (leadingText + PresetString.SPACE + text.drop(leadingLength))
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
                                                        val leadingText: String = bestScheme?.joinToString(separator = PresetString.SPACE) { it.text } ?: PresetString.EMPTY
                                                        when (leadingLength) {
                                                                0 -> text
                                                                text.length -> leadingText
                                                                else -> (leadingText + PresetString.SPACE + text.drop(leadingLength))
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
                                        val needsSymbols: Boolean = isEmojiSuggestionsOn.value && selectedCandidates.isEmpty()
                                        val asap: Boolean = userLexiconSuggestions.isNotEmpty()
                                        val suggestions = Engine.suggest(text = processingText, segmentation = segmentation, db = db, needsSymbols = needsSymbols, asap = asap)
                                        val mark: String = run {
                                                val userLexiconMark = userLexiconSuggestions.firstOrNull()?.mark
                                                if (userLexiconMark != null) {
                                                        userLexiconMark
                                                } else if (processingText.any { it.isSeparatorOrTone() }){
                                                        processingText.formattedForMark()
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
                bufferText = PresetString.EMPTY
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
        fun selectCandidate(candidate: Candidate? = null, index: Int = 0) {
                val candidate: Candidate = candidate ?: candidates.value.getOrNull(index) ?: return
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
                                        bufferText = PresetString.EMPTY
                                }
                        }
                        else -> {
                                val inputLength: Int = candidate.input.replace(Regex("([456])"), "RR").length
                                var tail = bufferText.drop(inputLength)
                                while (tail.startsWith(PresetCharacter.SEPARATOR)) {
                                        tail = tail.drop(1)
                                }
                                bufferText = tail
                        }
                }
        }
        fun backspace() {
                if (isBuffering.value) {
                        bufferText = bufferText.dropLast(1)
                        return
                }
                val noSelectedText: Boolean = currentInputConnection.getSelectedText(0).isNullOrEmpty()
                if (noSelectedText) {
                        val hasTextBeforeCursor: Boolean = currentInputConnection.getTextBeforeCursor(1, 0).isNullOrEmpty().not()
                        if (hasTextBeforeCursor) {
                                currentInputConnection.deleteSurroundingTextInCodePoints(1, 0)
                        }
                } else {
                        // Delete the selected text
                        currentInputConnection.commitText(PresetString.EMPTY, PresetString.EMPTY.length)
                }
        }
        fun forwardDelete() {
                val noSelectedText: Boolean = currentInputConnection.getSelectedText(0).isNullOrEmpty()
                if (noSelectedText) {
                        val hasTextAfterCursor: Boolean = currentInputConnection.getTextAfterCursor(1, 0).isNullOrEmpty().not()
                        if (hasTextAfterCursor) {
                                currentInputConnection.deleteSurroundingTextInCodePoints(0, 1)
                        }
                } else {
                        // Delete the selected text
                        currentInputConnection.commitText(PresetString.EMPTY, PresetString.EMPTY.length)
                }
        }
        fun performReturn() {
                if (isBuffering.value) {
                        currentInputConnection.commitText(bufferText, bufferText.length)
                        bufferText = PresetString.EMPTY
                        return
                }
                val imeOptions = currentInputEditorInfo.imeOptions
                val shouldInputNewLine: Boolean = (imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION) == EditorInfo.IME_FLAG_NO_ENTER_ACTION
                if (shouldInputNewLine){
                        currentInputConnection.commitText(PresetString.NEW_LINE, PresetString.NEW_LINE.length)
                        return
                }
                val hasActionLabel: Boolean = currentInputEditorInfo.actionLabel.isNullOrEmpty().not()
                val actionId = currentInputEditorInfo.actionId
                val hasSpecifiedActionId: Boolean = when (actionId) {
                        EditorInfo.IME_ACTION_UNSPECIFIED,
                        EditorInfo.IME_ACTION_NONE -> false
                        else -> true
                }
                val shouldPerformSpecifiedAction = hasActionLabel && hasSpecifiedActionId
                if (shouldPerformSpecifiedAction) {
                        currentInputConnection.performEditorAction(actionId)
                        return
                }
                val action = imeOptions and EditorInfo.IME_MASK_ACTION
                val isReasonableAction: Boolean = when (action) {
                        EditorInfo.IME_ACTION_UNSPECIFIED,
                        EditorInfo.IME_ACTION_NONE -> false
                        else -> true
                }
                if (isReasonableAction) {
                        currentInputConnection.performEditorAction(action)
                        return
                }
                currentInputConnection.commitText(PresetString.NEW_LINE, PresetString.NEW_LINE.length)
        }
        fun space() {
                if (isBuffering.value) {
                        if (candidates.value.isNotEmpty()) {
                                selectCandidate()
                        } else {
                                currentInputConnection.commitText(bufferText, bufferText.length)
                                bufferText = PresetString.EMPTY
                        }
                } else {
                        currentInputConnection.commitText(PresetString.SPACE, PresetString.SPACE.length)
                }
        }
        fun dismissKeyboard() {
                requestHideSelf(InputMethodManager.HIDE_NOT_ALWAYS)
        }
        fun leftKey() {
                if (isBuffering.value) {
                        bufferText += PresetString.SEPARATOR
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
                        bufferText += PresetString.SEPARATOR
                } else {
                        val text: String = when (inputMethodMode.value) {
                                InputMethodMode.Cantonese -> "。"
                                InputMethodMode.ABC -> "."
                        }
                        currentInputConnection.commitText(text, text.length)
                }
        }

        // region EditingPanel
        val isClipboardEmpty: MutableState<Boolean> by lazy {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                if (clipboard.hasPrimaryClip().not()) mutableStateOf(true)
                val hasText: Boolean = clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) == true
                mutableStateOf(hasText.not())
        }
        fun paste() {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                if (clipboard.hasPrimaryClip()) {
                        clipboard.primaryClip?.getItemAt(0)?.text?.let {
                                if (it.isNotEmpty()) {
                                        currentInputConnection.commitText(it, it.length)
                                }
                        }
                }
        }
        fun clearClipboard() {
                (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).clearPrimaryClip()
                isClipboardEmpty.value = true
        }
        fun copyAllText() {
                val request = ExtractedTextRequest()
                val extractedText = currentInputConnection.getExtractedText(request, 0)
                extractedText?.text?.let {
                        if (it.isEmpty()) return
                        val clip = ClipData.newPlainText(it, it)
                        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                        isClipboardEmpty.value = false
                }
        }
        fun cutAllText() {
                copyAllText()
                currentInputConnection.deleteSurroundingTextInCodePoints(1000, 1000)
        }
        fun clearAllText() {
                currentInputConnection.deleteSurroundingTextInCodePoints(1000, 0)
        }
        fun convertAllText() {
                val request = ExtractedTextRequest()
                val extractedText = currentInputConnection.getExtractedText(request, 0)
                val text = extractedText?.text?.toString()
                text?.let {
                        if (it.isEmpty()) return
                        val textLength = it.length
                        currentInputConnection.deleteSurroundingTextInCodePoints(textLength, textLength)
                        val simplified: String = it.convertedT2S()
                        val converted: String = if (simplified == it) it.convertedS2T() else simplified
                        currentInputConnection.commitText(converted, converted.length)
                }
        }
        fun moveBackward() {
                val keyDownEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT)
                currentInputConnection.sendKeyEvent(keyDownEvent)
                val keyUpEvent = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT)
                currentInputConnection.sendKeyEvent(keyUpEvent)
        }
        fun moveForward() {
                val keyDownEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT)
                currentInputConnection.sendKeyEvent(keyDownEvent)
                val keyUpEvent = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT)
                currentInputConnection.sendKeyEvent(keyUpEvent)
        }
        fun moveUpward() {
                val keyDownEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP)
                currentInputConnection.sendKeyEvent(keyDownEvent)
                val keyUpEvent = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP)
                currentInputConnection.sendKeyEvent(keyUpEvent)
        }
        fun moveDownward() {
                val keyDownEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN)
                currentInputConnection.sendKeyEvent(keyDownEvent)
                val keyUpEvent = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN)
                currentInputConnection.sendKeyEvent(keyUpEvent)
        }
        fun jump2head() {
                currentInputConnection.setSelection(0, 0)
        }
        fun jump2tail() {
                val request = ExtractedTextRequest()
                val extractedText = currentInputConnection.getExtractedText(request, 0)
                extractedText?.text?.length?.let {
                        currentInputConnection.setSelection(it, it)
                }
        }
}
