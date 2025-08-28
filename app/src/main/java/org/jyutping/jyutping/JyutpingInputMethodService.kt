package org.jyutping.jyutping

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.res.Configuration
import android.media.AudioManager
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jyutping.jyutping.emoji.Emoji
import org.jyutping.jyutping.emoji.EmojiCategory
import org.jyutping.jyutping.extensions.convertedS2T
import org.jyutping.jyutping.extensions.convertedT2S
import org.jyutping.jyutping.extensions.formattedCodePointText
import org.jyutping.jyutping.extensions.formattedForMark
import org.jyutping.jyutping.extensions.generateSymbol
import org.jyutping.jyutping.extensions.isReverseLookupTrigger
import org.jyutping.jyutping.extensions.isSeparatorOrTone
import org.jyutping.jyutping.extensions.toneConverted
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.keyboard.Candidate
import org.jyutping.jyutping.keyboard.Cangjie
import org.jyutping.jyutping.keyboard.CangjieVariant
import org.jyutping.jyutping.keyboard.CommentStyle
import org.jyutping.jyutping.keyboard.Engine
import org.jyutping.jyutping.keyboard.ExtraBottomPadding
import org.jyutping.jyutping.keyboard.InputMethodMode
import org.jyutping.jyutping.keyboard.KeyboardCase
import org.jyutping.jyutping.keyboard.KeyboardForm
import org.jyutping.jyutping.keyboard.KeyboardInterface
import org.jyutping.jyutping.keyboard.NumericLayout
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
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.DatabasePreparer
import org.jyutping.jyutping.utilities.ShapeKeyMap
import org.jyutping.jyutping.utilities.UserLexiconHelper

class JyutpingInputMethodService: LifecycleInputMethodService(),
        ViewModelStoreOwner,
        SavedStateRegistryOwner {

        override fun onEvaluateInputViewShown(): Boolean {
                super.onEvaluateInputViewShown()
                // Always show on-screen keyboard even if a hardware keyboard is connected
                return true
        }

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
                inputClientMonitorJob?.cancel()
                val isNightMode: Boolean = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                window?.window?.let {
                        WindowCompat.getInsetsController(it, it.decorView).isAppearanceLightNavigationBars = isNightMode.not()

                        @Suppress("DEPRECATION") // Needs for Android 14 and below
                        it.navigationBarColor = if (isDarkMode.value) {
                                if (isHighContrastPreferred.value) Color.Black.toArgb() else PresetColor.keyboardDarkBackground.toArgb()
                        } else {
                                if (isHighContrastPreferred.value) Color.White.toArgb() else PresetColor.keyboardLightBackground.toArgb()
                        }
                }
                isDarkMode.value = isNightMode
                inputMethodMode.value = InputMethodMode.Cantonese
                keyboardForm.value = KeyboardForm.Alphabetic
                qwertyForm.value = QwertyForm.Jyutping
                updateSpaceKeyForm()
                updateReturnKeyForm(attribute)
                inputClientMonitorJob = CoroutineScope(Dispatchers.Main).launch {
                        while (isActive) {
                                delay(500L) // 0.5s
                                monitorInputClient()
                        }
                }
        }
        override fun onFinishInputView(finishingInput: Boolean) {
                inputClientMonitorJob?.cancel()
                if (selectedCandidates.isNotEmpty()) {
                        selectedCandidates.clear()
                }
                if (isBuffering.value) {
                        currentInputConnection.commitText(bufferText, 1)
                        bufferText = PresetString.EMPTY
                }
                if (candidates.value.isNotEmpty()) {
                        candidates.value = emptyList()
                }
                super.onFinishInputView(finishingInput)
        }

        private var inputClientMonitorJob: Job? = null
        private fun monitorInputClient() {
                // TODO: Better way to monitor?
                if (isBuffering.value) {
                        val isTextEmpty = currentInputConnection?.getExtractedText(ExtractedTextRequest(), 0)?.text.isNullOrEmpty()
                        if (isTextEmpty) {
                                clearBuffer()
                        }
                }
        }

        override val viewModelStore: ViewModelStore
                get() = store
        override val lifecycle: Lifecycle
                get() = dispatcher.lifecycle

        private val store = ViewModelStore()

        private val savedStateRegistryController = SavedStateRegistryController.create(this)

        override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

        private val sharedPreferences by lazy { getSharedPreferences(UserSettingsKey.PreferencesFileName, MODE_PRIVATE) }

        val isDarkMode: MutableStateFlow<Boolean> by lazy {
                val isNightMode: Boolean = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                MutableStateFlow(isNightMode)
        }

        val spaceKeyForm: MutableStateFlow<SpaceKeyForm> by lazy { MutableStateFlow(SpaceKeyForm.Fallback) }
        private fun updateSpaceKeyForm() {
                val newForm: SpaceKeyForm = when {
                        inputMethodMode.value.isABC() -> SpaceKeyForm.English
                        keyboardForm.value == KeyboardForm.TenKeyNumeric -> SpaceKeyForm.Fallback
                        else -> {
                                val isSimplified: Boolean = characterStandard.value.isSimplified
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

        private val returnKeyForm: MutableStateFlow<ReturnKeyForm> by lazy { MutableStateFlow(ReturnKeyForm.StandbyTraditional) }
        val returnKeyText: MutableStateFlow<String?> by lazy { MutableStateFlow(null) }
        private fun updateReturnKeyForm(editorInfo: EditorInfo? = null) {
                val newForm: ReturnKeyForm = when (inputMethodMode.value) {
                        InputMethodMode.ABC -> ReturnKeyForm.StandbyABC
                        InputMethodMode.Cantonese -> {
                                if (isBuffering.value) {
                                        if (characterStandard.value.isSimplified) ReturnKeyForm.BufferingSimplified else ReturnKeyForm.BufferingTraditional
                                } else {
                                        if (characterStandard.value.isSimplified) ReturnKeyForm.StandbySimplified else ReturnKeyForm.StandbyTraditional
                                }
                        }
                }
                if (returnKeyForm.value != newForm) {
                        returnKeyForm.value = newForm
                }
                val imeAction = (editorInfo?.imeOptions ?: currentInputEditorInfo.imeOptions).and(EditorInfo.IME_MASK_ACTION)
                val newKeyText: String? = newForm.keyText(imeAction)
                if (returnKeyText.value != newKeyText) {
                        returnKeyText.value = newKeyText
                }
        }

        val inputMethodMode: MutableStateFlow<InputMethodMode> by lazy { MutableStateFlow(InputMethodMode.Cantonese) }
        fun toggleInputMethodMode() {
                val newMode: InputMethodMode = when (inputMethodMode.value) {
                        InputMethodMode.Cantonese -> InputMethodMode.ABC
                        InputMethodMode.ABC -> InputMethodMode.Cantonese
                }
                inputMethodMode.value = newMode
                updateSpaceKeyForm()
                updateReturnKeyForm()
        }

        val keyboardInterface: MutableStateFlow<KeyboardInterface> by lazy { MutableStateFlow(KeyboardInterface.PhonePortrait) }
        fun updateKeyboardInterface(keyboardInterface: KeyboardInterface) {
                if (this.keyboardInterface.value != keyboardInterface) {
                        this.keyboardInterface.value = keyboardInterface
                }
        }

        val qwertyForm: MutableStateFlow<QwertyForm> by lazy { MutableStateFlow(QwertyForm.Jyutping) }
        private fun updateQwertyForm(form: QwertyForm) {
                if (qwertyForm.value != form) {
                        qwertyForm.value = form
                }
        }

        val keyboardForm: MutableStateFlow<KeyboardForm> by lazy { MutableStateFlow(KeyboardForm.Alphabetic) }
        fun transformTo(destination: KeyboardForm) {
                if (isBuffering.value) {
                        val shouldKeepBuffer: Boolean = (destination == KeyboardForm.Alphabetic) || (destination == KeyboardForm.CandidateBoard)
                        if (shouldKeepBuffer.not()) {
                                currentInputConnection.commitText(bufferText, 1)
                                bufferText = PresetString.EMPTY
                        }
                }
                if (destination == KeyboardForm.EditingPanel) {
                        isClipboardEmpty.value = isCurrentClipboardEmpty()
                }
                keyboardForm.value = destination
                adjustKeyboardCase()
                updateSpaceKeyForm()
        }

        val keyboardCase: MutableStateFlow<KeyboardCase> by lazy { MutableStateFlow(KeyboardCase.Lowercased) }
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

        val characterStandard: MutableStateFlow<CharacterStandard> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.CharacterStandard, CharacterStandard.Traditional.identifier)
                val standard: CharacterStandard = CharacterStandard.standardOf(savedValue)
                MutableStateFlow(standard)
        }
        fun updateCharacterStandard(standard: CharacterStandard) {
                characterStandard.value = standard
                updateSpaceKeyForm()
                updateReturnKeyForm()
                sharedPreferences.edit {
                        putInt(UserSettingsKey.CharacterStandard, standard.identifier)
                }
        }
        val isAudioFeedbackOn: MutableStateFlow<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.AudioFeedback, 101)
                val isOn: Boolean = (savedValue == 101)
                MutableStateFlow(isOn)
        }
        fun updateAudioFeedback(isOn: Boolean) {
                isAudioFeedbackOn.value = isOn
                val value: Int = if (isOn) 101 else 102
                sharedPreferences.edit {
                        putInt(UserSettingsKey.AudioFeedback, value)
                }
        }
        val isHapticFeedbackOn: MutableStateFlow<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.HapticFeedback, 101)
                val isOn: Boolean = (savedValue == 101)
                MutableStateFlow(isOn)
        }
        fun updateHapticFeedback(isOn: Boolean) {
                isHapticFeedbackOn.value = isOn
                val value: Int = if (isOn) 101 else 102
                sharedPreferences.edit {
                        putInt(UserSettingsKey.HapticFeedback, value)
                }
        }
        val useTenKeyNumberPad: MutableStateFlow<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.NumericLayout, NumericLayout.Default.identifier)
                val shouldUseTenKeyNumberPad: Boolean = savedValue == NumericLayout.NumberKeyPad.identifier
                MutableStateFlow(shouldUseTenKeyNumberPad)
        }
        fun updateUseTenKeyNumberPad(isOn: Boolean) {
                useTenKeyNumberPad.value = isOn
                val value2save: Int = if (isOn) NumericLayout.NumberKeyPad.identifier else NumericLayout.Default.identifier
                sharedPreferences.edit {
                        putInt(UserSettingsKey.NumericLayout, value2save)
                }
        }
        val showLowercaseKeys: MutableStateFlow<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.KeyCase, 1)
                val isLowercase: Boolean = (savedValue == 1)
                MutableStateFlow(isLowercase)
        }
        fun updateShowLowercaseKeys(isOn: Boolean) {
                showLowercaseKeys.value = isOn
                val value2save: Int = if (isOn) 1 else 2
                sharedPreferences.edit {
                        putInt(UserSettingsKey.KeyCase, value2save)
                }
        }
        val previewKeyText: MutableStateFlow<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.KeyTextPreview, 1)
                val shouldPreview: Boolean = (savedValue == 1)
                MutableStateFlow(shouldPreview)
        }
        fun updatePreviewKeyText(isOn: Boolean) {
                previewKeyText.value = isOn
                val value2save: Int = if (isOn) 1 else 2
                sharedPreferences.edit {
                        putInt(UserSettingsKey.KeyTextPreview, value2save)
                }
        }
        val isHighContrastPreferred: MutableStateFlow<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.HighContrast, 0)
                val isPreferred: Boolean = (savedValue == 1)
                MutableStateFlow(isPreferred)
        }
        fun updateHighContrast(isOn: Boolean) {
                isHighContrastPreferred.value = isOn
                val value2save: Int = if (isOn) 1 else 2
                sharedPreferences.edit {
                        putInt(UserSettingsKey.HighContrast, value2save)
                }
        }
        val needsInputModeSwitchKey: MutableStateFlow<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.GlobeKey, 0)
                val needs: Boolean = (savedValue == 1)
                MutableStateFlow(needs)
        }
        fun updateNeedsInputModeSwitchKey(needs: Boolean) {
                needsInputModeSwitchKey.value = needs
                val value2save: Int = if (needs) 1 else 2
                sharedPreferences.edit {
                        putInt(UserSettingsKey.GlobeKey, value2save)
                }
        }
        val needsLeftKey: MutableStateFlow<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.LeftKey, 1)
                val needs: Boolean = (savedValue == 1)
                MutableStateFlow(needs)
        }
        fun updateNeedsLeftKey(needs: Boolean) {
                needsLeftKey.value = needs
                val value2save: Int = if (needs) 1 else 2
                sharedPreferences.edit {
                        putInt(UserSettingsKey.LeftKey, value2save)
                }
        }
        val needsRightKey: MutableStateFlow<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.RightKey, 1)
                val needs: Boolean = (savedValue == 1)
                MutableStateFlow(needs)
        }
        fun updateNeedsRightKey(needs: Boolean) {
                needsRightKey.value = needs
                val value2save: Int = if (needs) 1 else 2
                sharedPreferences.edit {
                        putInt(UserSettingsKey.RightKey, value2save)
                }
        }
        val extraBottomPadding: MutableStateFlow<ExtraBottomPadding> by lazy {
                val savedIdentifier: Int = sharedPreferences.getInt(UserSettingsKey.ExtraBottomPadding, ExtraBottomPadding.None.identifier)
                val paddingLevel = ExtraBottomPadding.paddingLevelOf(savedIdentifier)
                MutableStateFlow(paddingLevel)
        }
        fun updateExtraBottomPadding(paddingLevel: ExtraBottomPadding) {
                extraBottomPadding.value = paddingLevel
                sharedPreferences.edit {
                        putInt(UserSettingsKey.ExtraBottomPadding, paddingLevel.identifier)
                }
        }
        val isEmojiSuggestionsOn: MutableStateFlow<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.Emoji, 1)
                val isOn: Boolean = (savedValue == 1)
                MutableStateFlow(isOn)
        }
        fun updateEmojiSuggestionsState(isOn: Boolean) {
                isEmojiSuggestionsOn.value = isOn
                val value2save: Int = if (isOn) 1 else 2
                sharedPreferences.edit {
                        putInt(UserSettingsKey.Emoji, value2save)
                }
        }
        val commentStyle: MutableStateFlow<CommentStyle> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.CommentStyle, CommentStyle.AboveCandidates.identifier)
                val style: CommentStyle = CommentStyle.styleOf(savedValue)
                MutableStateFlow(style)
        }
        fun updateCommentStyle(style: CommentStyle) {
                commentStyle.value = style
                sharedPreferences.edit {
                        putInt(UserSettingsKey.CommentStyle, style.identifier)
                }
        }
        val cangjieVariant: MutableStateFlow<CangjieVariant> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.CangjieVariant, CangjieVariant.Cangjie5.identifier)
                val variant: CangjieVariant = CangjieVariant.variantOf(savedValue)
                MutableStateFlow(variant)
        }
        fun updateCangjieVariant(variant: CangjieVariant) {
                cangjieVariant.value = variant
                sharedPreferences.edit {
                        putInt(UserSettingsKey.CangjieVariant, variant.identifier)
                }
        }
        val isInputMemoryOn: MutableStateFlow<Boolean> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.InputMemory, 1)
                val isOn: Boolean = (savedValue == 1)
                MutableStateFlow(isOn)
        }
        fun updateInputMemoryState(isOn: Boolean) {
                isInputMemoryOn.value = isOn
                val value2save: Int = if (isOn) 1 else 2
                sharedPreferences.edit {
                        putInt(UserSettingsKey.InputMemory, value2save)
                }
        }

        private val selectedCandidates: MutableList<Candidate> by lazy { mutableListOf() }
        private val userDB by lazy { UserLexiconHelper(this) }
        fun forgetCandidate(candidate: Candidate? = null, index: Int? = null) = when {
                candidate != null -> userDB.remove(candidate)
                index != null -> candidates.value.getOrNull(index)?.let { userDB.remove(it) }
                else -> {}
        }
        fun clearInputMemory() {
                userDB.deleteAll()
                clearLocalEmojiFrequent()
        }

        //region EmojiBoard
        private val defaultFrequentEmojis: List<Emoji> by lazy { db.fetchDefaultFrequentEmojis() }
        private val emojiSequence: List<Emoji> by lazy { db.fetchEmojiSequence() }
        val categoryStartIndexMap: MutableStateFlow<Map<EmojiCategory, Int>> by lazy {
                MutableStateFlow(Emoji.categoryStartIndexMap(emojiSequence))
        }
        val emojiBoardEmojis: MutableStateFlow<List<Emoji>> by lazy {
                MutableStateFlow(frequentEmojis + emojiSequence)
        }
        private val frequentEmojis: MutableList<Emoji> by lazy {
                val savedText: String = (sharedPreferences.getString(UserSettingsKey.EmojiFrequent, PresetString.EMPTY) ?: PresetString.EMPTY)
                val codePointTexts: List<String> = savedText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val emojiTexts = codePointTexts.map { it.generateSymbol() }
                if (emojiTexts.count() != PresetConstant.FrequentEmojiCount) {
                        defaultFrequentEmojis.toMutableList()
                } else {
                        Emoji.generateFrequentEmojis(emojiTexts).toMutableList()
                }
        }
        fun updateEmojiFrequent(latest: Emoji) {
                val previous: List<String> = frequentEmojis.map { it.text }
                val combined: List<String> = (listOf(latest.text) + previous).distinct()
                if (combined.count() < PresetConstant.FrequentEmojiCount) return
                val update: List<String> = combined.take(PresetConstant.FrequentEmojiCount)
                val value2save: String = update.joinToString(separator = ",") { it.formattedCodePointText() }
                sharedPreferences.edit {
                        putString(UserSettingsKey.EmojiFrequent, value2save)
                }
                frequentEmojis.clear()
                val newFrequentEmojis = Emoji.generateFrequentEmojis(update)
                frequentEmojis.addAll(newFrequentEmojis)
                emojiBoardEmojis.value = newFrequentEmojis + emojiSequence
        }
        private fun clearLocalEmojiFrequent() {
                sharedPreferences.edit {
                        remove(UserSettingsKey.EmojiFrequent)
                }
                frequentEmojis.clear()
                frequentEmojis.addAll(defaultFrequentEmojis)
                emojiBoardEmojis.value = defaultFrequentEmojis + emojiSequence
        }
        //endregion

        val candidateState: MutableStateFlow<Int> by lazy { MutableStateFlow(1) }
        val candidates: MutableStateFlow<List<Candidate>> by lazy { MutableStateFlow(listOf()) }
        private val db by lazy { DatabaseHelper(this, DatabasePreparer.DATABASE_NAME) }
        private var bufferText: String = PresetString.EMPTY
                set(value) {
                        candidates.value = emptyList()
                        field = value
                        when (value.firstOrNull()) {
                                null -> {
                                        currentInputConnection.setComposingText(value, 0)
                                        currentInputConnection.finishComposingText()
                                        if (isBuffering.value) {
                                                if (isInputMemoryOn.value && selectedCandidates.isNotEmpty()) {
                                                        userDB.process(selectedCandidates)
                                                }
                                                selectedCandidates.clear()
                                                isBuffering.value = false
                                        }
                                        if (keyboardForm.value == KeyboardForm.CandidateBoard) {
                                                transformTo(KeyboardForm.Alphabetic)
                                        }
                                        updateQwertyForm(QwertyForm.Jyutping)
                                }
                                'r' -> {
                                        if (value.length < 2) {
                                                currentInputConnection.setComposingText(value, 1)
                                        } else {
                                                val text = value.drop(1)
                                                val segmentation = PinyinSegmentor.segment(text, db)
                                                val suggestions = Pinyin.reverseLookup(text, segmentation, db)
                                                val tailMark: String = run {
                                                        val firstCandidate = suggestions.firstOrNull()
                                                        if (firstCandidate != null && firstCandidate.inputCount == text.length) {
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
                                                currentInputConnection.setComposingText(mark, 1)
                                                candidates.value = suggestions.map { it.transformed(characterStandard.value, db) }.distinct()
                                        }
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                                'v' -> {
                                        updateQwertyForm(QwertyForm.Cangjie)
                                        if (value.length < 2) {
                                                currentInputConnection.setComposingText(value, 1)
                                        } else {
                                                val text = value.drop(1)
                                                val converted = text.mapNotNull { ShapeKeyMap.cangjieCode(it) }
                                                val isValidSequence: Boolean = converted.isNotEmpty() && (converted.size == text.length)
                                                if (isValidSequence) {
                                                        val mark = converted.joinToString(separator = PresetString.EMPTY)
                                                        currentInputConnection.setComposingText(mark, 1)
                                                        val suggestions = Cangjie.reverseLookup(text, cangjieVariant.value, db)
                                                        candidates.value = suggestions.map { it.transformed(characterStandard.value, db) }.distinct()
                                                } else {
                                                        currentInputConnection.setComposingText(bufferText, 1)
                                                }
                                        }
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                                'x' -> {
                                        updateQwertyForm(QwertyForm.Stroke)
                                        if (value.length < 2) {
                                                currentInputConnection.setComposingText(value, 1)
                                        } else {
                                                val text = value.drop(1)
                                                val transformed = ShapeKeyMap.strokeTransform(text)
                                                val converted = transformed.mapNotNull { ShapeKeyMap.strokeCode(it) }
                                                val isValidSequence: Boolean = converted.isNotEmpty() && (converted.size == text.length)
                                                if (isValidSequence) {
                                                        val mark = converted.joinToString(separator = PresetString.EMPTY)
                                                        currentInputConnection.setComposingText(mark, 1)
                                                        val suggestions = Stroke.reverseLookup(transformed, db)
                                                        candidates.value = suggestions.map { it.transformed(characterStandard.value, db) }.distinct()
                                                } else {
                                                        currentInputConnection.setComposingText(bufferText, 1)
                                                }
                                        }
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                                'q' -> {
                                        if (value.length < 2) {
                                                currentInputConnection.setComposingText(value, 1)
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
                                                currentInputConnection.setComposingText(mark, 1)
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
                                        val suggestions = Engine.suggest(origin = value, text = processingText, segmentation = segmentation, db = db, needsSymbols = needsSymbols, asap = asap)
                                        val mark: String = userLexiconSuggestions.firstOrNull()?.mark
                                                ?: if (processingText.any { it.isSeparatorOrTone() }) {
                                                        processingText.formattedForMark()
                                                } else {
                                                        val firstCandidate = suggestions.firstOrNull()
                                                        if (firstCandidate != null && firstCandidate.inputCount == processingText.length) firstCandidate.mark else processingText
                                                }
                                        currentInputConnection.setComposingText(mark, 1)
                                        candidates.value = (userLexiconSuggestions + suggestions).map { it.transformed(characterStandard.value, db) }.distinct()
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                        }
                        candidateState.value += 1
                        updateSpaceKeyForm()
                        updateReturnKeyForm()
                }
        val isBuffering: MutableStateFlow<Boolean> by lazy { MutableStateFlow(false) }
        fun clearBuffer() {
                bufferText = PresetString.EMPTY
        }
        fun process(text: String) {
                when (inputMethodMode.value) {
                        InputMethodMode.Cantonese -> {
                                bufferText += text
                        }
                        InputMethodMode.ABC -> {
                                currentInputConnection.commitText(text, 1)
                        }
                }
                adjustKeyboardCase()
        }
        fun input(text: String) {
                currentInputConnection.commitText(text, 1)
                adjustKeyboardCase()
        }
        fun selectCandidate(candidate: Candidate? = null, index: Int = 0) {
                val item: Candidate = candidate ?: candidates.value.getOrNull(index) ?: return
                currentInputConnection.commitText(item.text, 1)
                selectedCandidates.add(item)
                val firstChar = bufferText.firstOrNull()
                when {
                        (firstChar == null) -> {}
                        firstChar.isReverseLookupTrigger() -> {
                                val leadingLength = item.inputCount + 1
                                if (bufferText.length > leadingLength) {
                                        val tail = bufferText.drop(leadingLength)
                                        bufferText = "${firstChar}${tail}"
                                } else {
                                        bufferText = PresetString.EMPTY
                                }
                        }
                        else -> {
                                val inputLength: Int = item.input.replace(Regex("([456])"), "RR").length
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
                        sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL)
                        /*
                        val hasTextBeforeCursor: Boolean = currentInputConnection.getTextBeforeCursor(1, 0).isNullOrEmpty().not()
                        if (hasTextBeforeCursor) {
                                currentInputConnection.deleteSurroundingTextInCodePoints(1, 0)
                        }
                        */
                } else {
                        currentInputConnection.commitText(PresetString.EMPTY, 0)
                }
        }
        fun forwardDelete() {
                val noSelectedText: Boolean = currentInputConnection.getSelectedText(0).isNullOrEmpty()
                if (noSelectedText) {
                        sendDownUpKeyEvents(KeyEvent.KEYCODE_FORWARD_DEL)
                        /*
                        val hasTextAfterCursor: Boolean = currentInputConnection.getTextAfterCursor(1, 0).isNullOrEmpty().not()
                        if (hasTextAfterCursor) {
                                currentInputConnection.deleteSurroundingTextInCodePoints(0, 1)
                        }
                        */
                } else {
                        currentInputConnection.commitText(PresetString.EMPTY, 0)
                }
        }
        fun performReturn() {
                if (isBuffering.value) {
                        currentInputConnection.commitText(bufferText, 1)
                        bufferText = PresetString.EMPTY
                        return
                }
                val imeOptions = currentInputEditorInfo.imeOptions
                val shouldInputNewLine: Boolean = (imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION) == EditorInfo.IME_FLAG_NO_ENTER_ACTION
                if (shouldInputNewLine){
                        currentInputConnection.commitText(PresetString.NEW_LINE, 1)
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
                currentInputConnection.commitText(PresetString.NEW_LINE, 1)
        }
        fun space() {
                if (isBuffering.value) {
                        if (candidates.value.isNotEmpty()) {
                                candidates.value.firstOrNull()?.let { selectCandidate(it) }
                        } else {
                                currentInputConnection.commitText(bufferText, 1)
                                bufferText = PresetString.EMPTY
                        }
                } else {
                        currentInputConnection.commitText(PresetString.SPACE, 1)
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
                        currentInputConnection.commitText(text, 1)
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
                        currentInputConnection.commitText(text, 1)
                }
        }

        //region EditingPanel
        val isClipboardEmpty: MutableStateFlow<Boolean> by lazy { MutableStateFlow(true) }
        private fun isCurrentClipboardEmpty(): Boolean {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                if (clipboard.hasPrimaryClip().not()) return true
                val hasText: Boolean = clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) == true
                return hasText.not()
        }
        fun paste() {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                if (clipboard.hasPrimaryClip()) {
                        clipboard.primaryClip?.getItemAt(0)?.text?.let {
                                if (it.isNotEmpty()) {
                                        currentInputConnection.commitText(it, 1)
                                }
                        }
                }
        }
        fun clearClipboard() {
                (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).clearPrimaryClip()
                isClipboardEmpty.value = true
        }
        fun copyAllText() {
                val request = ExtractedTextRequest()
                val extractedText = currentInputConnection.getExtractedText(request, 0)
                extractedText?.text?.let {
                        if (it.isEmpty()) return
                        val clip = ClipData.newPlainText(it, it)
                        (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                        isClipboardEmpty.value = false
                }
        }
        fun cutAllText() {
                currentInputConnection.performContextMenuAction(android.R.id.selectAll)
                val selectedText = currentInputConnection.getSelectedText(0)
                selectedText?.let {
                        if (it.isEmpty()) return
                        val clip = ClipData.newPlainText(it, it)
                        (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                        isClipboardEmpty.value = false
                }
                currentInputConnection.commitText(PresetString.EMPTY, 0)
        }
        fun clearAllText() {
                val textLengthBeforeCursor = currentInputConnection.getTextBeforeCursor(1000, 0)?.length
                if (textLengthBeforeCursor != null) {
                        currentInputConnection.deleteSurroundingText(textLengthBeforeCursor, 0)
                } else {
                        currentInputConnection.performContextMenuAction(android.R.id.selectAll)
                        currentInputConnection.commitText(PresetString.EMPTY, 0)
                }
        }
        fun convertAllText() {
                currentInputConnection.performContextMenuAction(android.R.id.selectAll)
                val selectedText = currentInputConnection.getSelectedText(0)
                selectedText?.let {
                        if (it.isEmpty()) return
                        val origin: String = it.toString()
                        val simplified: String = origin.convertedT2S()
                        val converted: String = if (simplified == origin) origin.convertedS2T() else simplified
                        currentInputConnection.commitText(converted, 1)
                }
        }
        fun moveBackward() {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
        }
        fun moveForward() {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT)
        }
        fun moveUpward() {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_UP)
        }
        fun moveDownward() {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_DOWN)
        }
        fun jump2head() {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_MOVE_HOME)
                // currentInputConnection.setSelection(0, 0)
        }
        fun jump2tail() {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_MOVE_END)
                /*
                val request = ExtractedTextRequest()
                val extractedText = currentInputConnection.getExtractedText(request, 0)
                extractedText?.text?.length?.let {
                        currentInputConnection.setSelection(it, it)
                }
                */
        }
        //endregion

        //region Keyboard Feedback
        private val audioManager by lazy { getSystemService(AUDIO_SERVICE) as AudioManager }
        fun audioFeedback(effect: SoundEffect) {
                if (isAudioFeedbackOn.value) {
                        audioManager.playSoundEffect(effect.soundId(), -1f)
                }
        }
        //endregion
}
