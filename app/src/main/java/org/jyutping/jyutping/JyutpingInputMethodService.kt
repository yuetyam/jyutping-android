package org.jyutping.jyutping

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
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
import org.jyutping.jyutping.extensions.generateSymbol
import org.jyutping.jyutping.extensions.markFormatted
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.extensions.toneConverted
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.keyboard.Candidate
import org.jyutping.jyutping.keyboard.Cangjie
import org.jyutping.jyutping.keyboard.CangjieVariant
import org.jyutping.jyutping.keyboard.CommentStyle
import org.jyutping.jyutping.keyboard.ExtraBottomPadding
import org.jyutping.jyutping.models.InputMethodMode
import org.jyutping.jyutping.models.KeyboardCase
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.models.KeyboardInterface
import org.jyutping.jyutping.models.KeyboardLayout
import org.jyutping.jyutping.keyboard.NumericLayout
import org.jyutping.jyutping.keyboard.PhysicalKeyMapper
import org.jyutping.jyutping.keyboard.Pinyin
import org.jyutping.jyutping.keyboard.PinyinSegmentor
import org.jyutping.jyutping.keyboard.QwertyForm
import org.jyutping.jyutping.keyboard.ReturnKeyForm
import org.jyutping.jyutping.keyboard.SpaceKeyForm
import org.jyutping.jyutping.keyboard.Stroke
import org.jyutping.jyutping.models.Structure
import org.jyutping.jyutping.keyboard.transformed
import org.jyutping.jyutping.models.BasicInputEvent
import org.jyutping.jyutping.models.Converter
import org.jyutping.jyutping.models.InputKeyEvent
import org.jyutping.jyutping.models.Researcher
import org.jyutping.jyutping.models.Segmenter
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.models.length
import org.jyutping.jyutping.models.mark
import org.jyutping.jyutping.models.searchSymbols
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.DatabasePreparer
import org.jyutping.jyutping.utilities.ShapeKeyMap
import org.jyutping.jyutping.utilities.UserLexiconHelper
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class JyutpingInputMethodService: LifecycleInputMethodService(),
        ViewModelStoreOwner,
        SavedStateRegistryOwner {

        override fun onEvaluateInputViewShown(): Boolean {
                super.onEvaluateInputViewShown()
                // Always show on-screen keyboard even if a hardware keyboard is connected
                return true
        }

        /**
         * Prevent the system from switching to the full-screen extract editor UI.
         * Some devices/hosts cause the IME to enter a full-screen text box (with a submit
         * button) in landscape. Returning false forces the IME to keep the input view
         * anchored (inline) instead of using the extract/fullscreen UI.
         */
        override fun onEvaluateFullscreenMode() = false

        private var canBlurWindow: Boolean = false
        private val blurBlackList: HashSet<String> by lazy { hashSetOf("zte", "nubia", "redmagic", "red magic") }
        override fun onCreate() {
                super.onCreate()
                savedStateRegistryController.performRestore(null)
                DatabasePreparer.prepare(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val manufacturer = Build.MANUFACTURER.lowercase()
                        val brand = Build.BRAND.lowercase()
                        val shouldDisableBlur: Boolean = blurBlackList.contains(manufacturer) || blurBlackList.contains(brand)
                        canBlurWindow = if (shouldDisableBlur) { false } else {
                                getSystemService(WindowManager::class.java)?.isCrossWindowBlurEnabled ?: false
                        }
                        PresetColor.attach(canBlur = canBlurWindow)
                        if (canBlurWindow) {
                                window?.window?.let { win ->
                                        win.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
                                        win.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                                        val density = resources.displayMetrics.density
                                        val blurPixel = (24 * density).roundToInt()
                                        win.setBackgroundBlurRadius(blurPixel)
                                }
                        }
                } else {
                        canBlurWindow = false
                        PresetColor.attach(canBlur = false)
                }
        }
        override fun onConfigurationChanged(newConfig: Configuration) {
                super.onConfigurationChanged(newConfig)

                // Update view state based on hardware keyboard availability
                if (hasHardwareKeyboard()) {
                        if (!isPhysicalKeyboardActive.value) {
                                showPhysicalKeyboardCandidates()
                        }
                } else {
                        if (isPhysicalKeyboardActive.value) {
                                showSoftKeyboard()
                        }
                }
        }

        override fun onCreateInputView(): View {
                window?.window?.decorView?.let { decorView ->
                        decorView.setViewTreeLifecycleOwner(this)
                        decorView.setViewTreeViewModelStoreOwner(this)
                        decorView.setViewTreeSavedStateRegistryOwner(this)
                }
                if (canBlurWindow) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val canNowBlur: Boolean = getSystemService(WindowManager::class.java)?.isCrossWindowBlurEnabled ?: false
                                if (canNowBlur.negative) {
                                        canBlurWindow = false
                                        PresetColor.attach(canBlur = false)
                                }
                        } else {
                                canBlurWindow = false
                                PresetColor.attach(canBlur = false)
                        }
                }
                return ComposeKeyboardView(this)
        }
        override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
                super.onStartInput(attribute, restarting)
                inputClientMonitorJob?.cancel()

                // Update the flag for hardware keyboard state
                isPhysicalKeyboardActive.value = hasHardwareKeyboard()

                val isNightMode: Boolean = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                window?.window?.let { win ->
                        WindowCompat.getInsetsController(win, win.decorView).isAppearanceLightNavigationBars = isNightMode.negative
                        val bgColor: Color = if (isHighContrastPreferred.value) {
                                if (isNightMode) AltPresetColor.darkBackground else AltPresetColor.lightBackground
                        } else {
                                if (isNightMode) PresetColor.darkBackground else PresetColor.lightBackground
                        }
                        val barColor: Color = if (canBlurWindow) bgColor else bgColor.copy(alpha = 1f)
                        @Suppress("DEPRECATION")
                        win.navigationBarColor = barColor.toArgb()
                }
                isDarkMode.value = isNightMode
                inputMethodMode.value = InputMethodMode.Cantonese
                keyboardForm.value = KeyboardForm.Alphabetic
                qwertyForm.value = if (keyboardLayout.value.isTripleStroke) QwertyForm.TripleStroke else QwertyForm.Jyutping
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
                isPhysicalKeyboardActive.value = false
                if (selectedCandidates.isNotEmpty()) {
                        selectedCandidates.clear()
                }
                if (isBuffering.value) {
                        val text = joinedBufferTexts()
                        currentInputConnection.commitText(text, 1)
                        clearBuffer()
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

        // References to views
        val isPhysicalKeyboardActive = MutableStateFlow(false)

        // Check if physical/hardware keyboard is available
        private fun hasHardwareKeyboard(): Boolean {
                val config = resources.configuration
                return config.keyboard != Configuration.KEYBOARD_NOKEYS &&
                       config.hardKeyboardHidden != Configuration.HARDKEYBOARDHIDDEN_YES
        }

        fun showPhysicalKeyboardCandidates() {
                isPhysicalKeyboardActive.value = true
        }

        fun showSoftKeyboard() {
                isPhysicalKeyboardActive.value = false
        }

        private val sharedPreferences by lazy { getSharedPreferences(UserSettingsKey.PreferencesFileName, MODE_PRIVATE) }

        val isDarkMode: MutableStateFlow<Boolean> by lazy {
                val isNightMode: Boolean = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                MutableStateFlow(isNightMode)
        }

        // Last physical key pressed (for UI preview)
        val lastPhysicalKey: MutableStateFlow<InputKeyEvent?> by lazy { MutableStateFlow(null) }

        // Candidate offset for physical keyboard number selection (tracks which group of 3 to show 7-9)
        val candidateOffset: MutableStateFlow<Int> by lazy { MutableStateFlow(0) }

        // Track if a key was pressed while Shift was down (to distinguish Shift-only press from Shift+Key)
        private var keyPressedDuringShift = false

        private fun emitPhysicalKeyPreview(inputKey: InputKeyEvent) {
                lastPhysicalKey.value = inputKey
                // audio/haptic feedback
                audioFeedback(SoundEffect.Click)
        }

        val spaceKeyForm: MutableStateFlow<SpaceKeyForm> by lazy { MutableStateFlow(SpaceKeyForm.Fallback) }
        private fun updateSpaceKeyForm() {
                val newForm: SpaceKeyForm = when {
                        inputMethodMode.value.isABC -> SpaceKeyForm.English
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

        val qwertyForm: MutableStateFlow<QwertyForm> by lazy {
                val form: QwertyForm = if (keyboardLayout.value.isTripleStroke) QwertyForm.TripleStroke else QwertyForm.Jyutping
                MutableStateFlow(form)
        }
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
                                val text = joinedBufferTexts()
                                currentInputConnection.commitText(text, 1)
                                clearBuffer()
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
                if (keyboardCase.value.isUppercased) {
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
        val keyboardLayout: MutableStateFlow<KeyboardLayout> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.KeyboardLayout, KeyboardLayout.Qwerty.identifier)
                val layout = KeyboardLayout.layoutOf(savedValue)
                MutableStateFlow(layout)
        }
        fun updateKeyboardLayout(layout: KeyboardLayout) {
                keyboardLayout.value = layout
                val newForm: QwertyForm = if (layout.isTripleStroke) QwertyForm.TripleStroke else QwertyForm.Jyutping
                updateQwertyForm(newForm)
                sharedPreferences.edit {
                        putInt(UserSettingsKey.KeyboardLayout, layout.identifier)
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
        val keyHeightOffset: MutableStateFlow<Int> by lazy {
                val savedValue: Int = sharedPreferences.getInt(UserSettingsKey.KeyHeightOffset, 0)
                MutableStateFlow(savedValue)
        }
        fun updateKeyHeightOffset(offset: Int) {
                keyHeightOffset.value = offset
                sharedPreferences.edit {
                        putInt(UserSettingsKey.KeyHeightOffset, offset)
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

        private var bufferEvents: List<BasicInputEvent> by Delegates.observable(emptyList()) { _, _, newValue ->
                candidates.value = emptyList()
                candidateOffset.value = 0
                when (newValue.firstOrNull()?.key) {
                        null -> {
                                currentInputConnection.setComposingText(PresetString.EMPTY, 1)
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
                                val newForm: QwertyForm = if (keyboardLayout.value.isTripleStroke) QwertyForm.TripleStroke else QwertyForm.Jyutping
                                updateQwertyForm(newForm)
                        }
                        VirtualInputKey.letterR -> {
                                updateQwertyForm(QwertyForm.Pinyin)
                                val inputText = joinedBufferTexts()
                                if (inputText.length < 2) {
                                        currentInputConnection.setComposingText(inputText, 1)
                                } else {
                                        val text = inputText.drop(1)
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
                        VirtualInputKey.letterV -> {
                                updateQwertyForm(QwertyForm.Cangjie)
                                val inputText = joinedBufferTexts()
                                if (inputText.length < 2) {
                                        currentInputConnection.setComposingText(inputText, 1)
                                } else {
                                        val text = inputText.drop(1)
                                        val converted = text.mapNotNull { ShapeKeyMap.cangjieCode(it.toString()) }
                                        val isValidSequence: Boolean = converted.isNotEmpty() && (converted.size == text.count())
                                        if (isValidSequence) {
                                                val mark = converted.joinToString(separator = PresetString.EMPTY)
                                                currentInputConnection.setComposingText(mark, 1)
                                                val suggestions = Cangjie.reverseLookup(text, cangjieVariant.value, db)
                                                candidates.value = suggestions.map { it.transformed(characterStandard.value, db) }.distinct()
                                        } else {
                                                currentInputConnection.setComposingText(inputText, 1)
                                        }
                                }
                                if (isBuffering.value.not()) {
                                        isBuffering.value = true
                                }
                        }
                        VirtualInputKey.letterX -> {
                                updateQwertyForm(QwertyForm.Stroke)
                                val inputText = joinedBufferTexts()
                                if (inputText.length < 2) {
                                        currentInputConnection.setComposingText(inputText, 1)
                                } else {
                                        val text = inputText.drop(1)
                                        val transformed = ShapeKeyMap.strokeTransform(text)
                                        val converted = transformed.mapNotNull { ShapeKeyMap.strokeCode(it) }
                                        val isValidSequence: Boolean = converted.isNotEmpty() && (converted.size == text.length)
                                        if (isValidSequence) {
                                                val mark = converted.joinToString(separator = PresetString.EMPTY)
                                                currentInputConnection.setComposingText(mark, 1)
                                                val suggestions = Stroke.reverseLookup(transformed, db)
                                                candidates.value = suggestions.map { it.transformed(characterStandard.value, db) }.distinct()
                                        } else {
                                                currentInputConnection.setComposingText(inputText, 1)
                                        }
                                }
                                if (isBuffering.value.not()) {
                                        isBuffering.value = true
                                }
                        }
                        VirtualInputKey.letterQ -> {
                                val inputText = joinedBufferTexts()
                                if (inputText.length < 2) {
                                        currentInputConnection.setComposingText(inputText, 1)
                                } else {
                                        val keys = bufferEvents.drop(1).map { it.key }
                                        val segmentation = Segmenter.segment(keys, db)
                                        val text = inputText.drop(1)
                                        val tailMark: String = run {
                                                val bestScheme = segmentation.firstOrNull()
                                                val leadingLength: Int = bestScheme?.length ?: 0
                                                val leadingMark: String = bestScheme?.mark ?: PresetString.EMPTY
                                                when (leadingLength) {
                                                        0 -> text
                                                        text.length -> leadingMark
                                                        else -> (leadingMark + PresetString.SPACE + text.drop(leadingLength))
                                                }
                                        }
                                        val mark: String = inputText.take(1) + PresetString.SPACE + tailMark
                                        currentInputConnection.setComposingText(mark, 1)
                                        val suggestions = Structure.reverseLookup(text, segmentation, db)
                                        candidates.value = suggestions.map { it.transformed(characterStandard.value, db) }.distinct()
                                }
                                if (isBuffering.value.negative) {
                                        isBuffering.value = true
                                }
                        }
                        else -> {
                                val keys = newValue.map { it.key }
                                val text = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                                val segmentation = Segmenter.segment(keys, db)
                                val memory = if (isInputMemoryOn.value) userDB.suggest(text = text, segmentation = segmentation) else emptyList()
                                val symbols = if (isEmojiSuggestionsOn.value) db.searchSymbols(text = text, segmentation = segmentation) else emptyList()
                                val queried = Researcher.suggest(keys = keys, segmentation = segmentation, db = db)
                                val suggestions = Converter.dispatch(memory = memory, symbols = symbols, queried = queried, standard = characterStandard.value, db = db)
                                val mark: String = run {
                                        val hasOtherMarks = keys.any { it.isSyllableLetter.negative }
                                        if (hasOtherMarks) {
                                                text.toneConverted().markFormatted()
                                        } else {
                                                val firstCandidate = suggestions.firstOrNull()
                                                if (firstCandidate?.inputCount == text.length) {
                                                        firstCandidate.mark
                                                } else {
                                                        text.toneConverted().markFormatted()
                                                }
                                        }
                                }
                                currentInputConnection.setComposingText(mark, 1)
                                candidates.value = suggestions
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
                bufferEvents = emptyList()
        }
        private fun joinedBufferTexts(): String = bufferEvents.joinToString(separator = PresetString.EMPTY) { if (it.case.isLowercased) it.key.text else it.key.text.uppercase() }
        fun handle(key: VirtualInputKey) {
                val shouldAppendEvent: Boolean = inputMethodMode.value.isCantonese && keyboardForm.value.isBufferable
                if (shouldAppendEvent) {
                        val newEvent = BasicInputEvent(key = key, case = keyboardCase.value)
                        bufferEvents = bufferEvents + newEvent
                } else {
                        val text: String = if (keyboardCase.value.isLowercased) key.text else key.text.uppercase()
                        currentInputConnection.commitText(text, 1)
                }
                adjustKeyboardCase()
        }
        fun process(text: String) {
                val shouldAppendEvents: Boolean = inputMethodMode.value.isCantonese && keyboardForm.value.isBufferable
                if (shouldAppendEvents) {
                        val case = keyboardCase.value
                        val newEvents = text.mapNotNull { VirtualInputKey.matchVirtualInputKey(it) }.map { BasicInputEvent(key = it, case = case) }
                        bufferEvents = bufferEvents + newEvents
                } else {
                        currentInputConnection.commitText(text, 1)
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
                val firstKey = bufferEvents.firstOrNull()?.key ?: return
                if (firstKey.isReverseLookupTrigger) {
                        selectedCandidates.clear()
                        var tail = bufferEvents.drop(item.inputCount + 1)
                        while (tail.firstOrNull()?.key?.isApostrophe ?: false) {
                                tail = tail.drop(1)
                        }
                        val tailLength = tail.size
                        if (tailLength < 1) {
                                clearBuffer()
                        } else {
                                bufferEvents = bufferEvents.take(1) + bufferEvents.takeLast(tailLength)
                        }
                } else {
                        if (item.type.isCantonese()) {
                                selectedCandidates.add(item)
                        }
                        var tail = bufferEvents.drop(item.inputCount)
                        while (tail.firstOrNull()?.key?.isApostrophe ?: false) {
                                tail = tail.drop(1)
                        }
                        val tailLength = tail.size
                        if (tailLength < 1) {
                                clearBuffer()
                        } else {
                                bufferEvents = bufferEvents.takeLast(tailLength)
                        }
                }
        }
        fun backspace() {
                if (isBuffering.value) {
                        bufferEvents = bufferEvents.dropLast(1)
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
                        currentInputConnection.commitText(PresetString.EMPTY, 1)
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
                        currentInputConnection.commitText(PresetString.EMPTY, 1)
                }
        }
        fun performReturn() {
                if (isBuffering.value) {
                        val text = joinedBufferTexts()
                        currentInputConnection.commitText(text, 1)
                        clearBuffer()
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
                                val text = joinedBufferTexts()
                                currentInputConnection.commitText(text, 1)
                                clearBuffer()
                        }
                } else {
                        currentInputConnection.commitText(PresetString.SPACE, 1)
                }
        }
        fun dismissKeyboard() {
                requestHideSelf(InputMethodManager.HIDE_NOT_ALWAYS)
        }

        // New: Physical keyboard support - central handler
        override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
                // If we handled the event, consume it; otherwise let super handle (so system/app shortcuts work)
                return if (handlePhysicalKeyEvent(event)) true else super.onKeyDown(keyCode, event)
        }

        override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
                // Handle Shift key release for input mode toggle
                if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
                        // Only toggle input mode if no other key was pressed during Shift
                        // (i.e., it was a standalone Shift press, not Shift+letter for capitalization)
                        if (!keyPressedDuringShift) {
                                toggleInputMethodMode()
                        }
                        // Reset the flag for next Shift press
                        keyPressedDuringShift = false
                        return true
                }
                return super.onKeyUp(keyCode, event)
        }

        /**
         * Handle a physical KeyEvent. Returns true when the IME consumed the event.
         * Policy: do not intercept Ctrl/Meta combinations; leave them to the host app.
         */
        private fun handlePhysicalKeyEvent(event: KeyEvent): Boolean {
                // Pass through when control/meta keys are pressed (shortcuts)
                if (event.isCtrlPressed || event.isMetaPressed) return false

                // Track Shift key down (but don't toggle mode yet - wait for key up)
                if (event.keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || event.keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
                        if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                                // Just mark that Shift is down, don't toggle yet
                                return true
                        }
                        return false // Let system handle
                }

                // Handle Tab key to cycle through candidate groups (for number selection 7-9)
                if (event.keyCode == KeyEvent.KEYCODE_TAB) {
                        if (event.isShiftPressed) keyPressedDuringShift = true

                        val candidateCount = candidates.value.size
                        if (candidateCount > 0) {
                                if (event.isShiftPressed) {
                                        // Shift+Tab: go back 3 candidates
                                        val currentOffset = candidateOffset.value
                                        if (currentOffset > 0) {
                                                val newOffset = maxOf(0, currentOffset - 3)
                                                candidateOffset.value = newOffset
                                                audioFeedback(SoundEffect.Click)
                                                return true
                                        }
                                        // At index 0, do nothing
                                        return true
                                } else {
                                        // Tab: move to next group of 3
                                        val newOffset = candidateOffset.value + 3
                                        candidateOffset.value = if (newOffset >= candidateCount) 0 else newOffset
                                        audioFeedback(SoundEffect.Click)
                                        return true
                                }
                        }
                        return false
                }

                // Handle number keys 7-9 to select candidates, as jyutping does use tone 7-9, only tone 1-6 are used
                when (event.keyCode) {
                        KeyEvent.KEYCODE_7 -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                val index = candidateOffset.value
                                if (index < candidates.value.size) {
                                        selectCandidate(index = index)
                                        return true
                                }
                        }
                        KeyEvent.KEYCODE_8 -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                val index = candidateOffset.value + 1
                                if (index < candidates.value.size) {
                                        selectCandidate(index = index)
                                        return true
                                }
                        }
                        KeyEvent.KEYCODE_9 -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                val index = candidateOffset.value + 2
                                if (index < candidates.value.size) {
                                        selectCandidate(index = index)
                                        return true
                                }
                        }
                }

                // Handle special non-printable keys first
                when (event.keyCode) {
                        KeyEvent.KEYCODE_DEL -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                backspace()
                                return true
                        }
                        KeyEvent.KEYCODE_FORWARD_DEL -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                forwardDelete()
                                return true
                        }
                        KeyEvent.KEYCODE_ENTER -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                performReturn()
                                return true
                        }
                        KeyEvent.KEYCODE_SPACE -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                space()
                                return true
                        }
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                moveBackward()
                                return true
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                moveForward()
                                return true
                        }
                        KeyEvent.KEYCODE_DPAD_UP -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                moveUpward()
                                return true
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                moveDownward()
                                return true
                        }
                        KeyEvent.KEYCODE_MOVE_HOME -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                jump2head()
                                return true
                        }
                        KeyEvent.KEYCODE_MOVE_END -> {
                                if (event.isShiftPressed) keyPressedDuringShift = true
                                jump2tail()
                                return true
                        }
                }

                // Use KeyCharacterMap to get the actual character from the physical keyboard
                // This respects the keyboard layout and modifier keys (Shift, Alt, etc.)
                val unicodeChar = event.unicodeChar
                if (unicodeChar != 0 && !Character.isISOControl(unicodeChar)) {
                        // Mark that a key was pressed while Shift is down
                        if (event.isShiftPressed) {
                                keyPressedDuringShift = true
                        }

                        // Show physical keyboard candidates view
                        if (!isPhysicalKeyboardActive.value) {
                                showPhysicalKeyboardCandidates()
                        }

                        val char = unicodeChar.toChar().toString()

                        // In ABC mode, commit the character directly
                        // In Cantonese mode, for letters a-z try to use the IME handler
                        when (inputMethodMode.value) {
                                InputMethodMode.ABC -> {
                                        currentInputConnection.commitText(char, 1)
                                }
                                InputMethodMode.Cantonese -> {
                                        // For lowercase letters, try using the mapped InputKeyEvent
                                        val mapped = if (char.length == 1 && char[0] in 'a'..'z') {
                                                PhysicalKeyMapper.map(event.keyCode)
                                        } else null

                                        if (mapped != null) {
                                                // Feed into existing IME handler for Cantonese input
                                                handle(mapped)
                                        } else {
                                                // For symbols and other characters, commit directly
                                                currentInputConnection.commitText(char, 1)
                                        }
                                }
                        }

                        // Emit feedback
                        audioFeedback(SoundEffect.Click)
                        return true
                }

                // Map printable keys using PhysicalKeyMapper
                val mapped: InputKeyEvent? = PhysicalKeyMapper.map(event.keyCode)
                if (mapped != null) {
                        // Mark that a key was pressed while Shift is down (for Shift toggle detection)
                        if (event.isShiftPressed) {
                                keyPressedDuringShift = true
                        }

                        // Show physical keyboard candidates view for physical typing
                        if (!isPhysicalKeyboardActive.value) {
                                showPhysicalKeyboardCandidates()
                        }

                        // Respect Shift/Caps Lock for ABC mode; Cantonese mode typically uses lowercased letters
                        // Check both Shift key state and Caps Lock state
                        val useUpper = (event.isShiftPressed || event.isCapsLockOn || keyboardCase.value.isUppercased)
                        val textToCommit = if (useUpper && inputMethodMode.value.isABC) mapped.text.uppercase() else mapped.text

                        when (inputMethodMode.value) {
                                InputMethodMode.ABC -> {
                                        currentInputConnection.commitText(textToCommit, 1)
                                }
                                InputMethodMode.Cantonese -> {
                                        // Feed into existing IME handler to maintain buffering/candidate logic
                                        handle(mapped)
                                }
                        }

                        // Emit preview and feedback
                        emitPhysicalKeyPreview(mapped)
                        return true
                }

                // Unmapped: let the system handle it
                return false
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
                currentInputConnection.commitText(PresetString.EMPTY, 1)
        }
        fun clearAllText() {
                val textLengthBeforeCursor = currentInputConnection.getTextBeforeCursor(1000, 0)?.length
                if (textLengthBeforeCursor != null) {
                        currentInputConnection.deleteSurroundingText(textLengthBeforeCursor, 0)
                } else {
                        currentInputConnection.performContextMenuAction(android.R.id.selectAll)
                        currentInputConnection.commitText(PresetString.EMPTY, 1)
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
