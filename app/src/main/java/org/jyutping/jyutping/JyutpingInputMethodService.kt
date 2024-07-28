package org.jyutping.jyutping

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
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
import org.jyutping.jyutping.extensions.keyboardLightBackground
import org.jyutping.jyutping.extensions.separator
import org.jyutping.jyutping.extensions.separatorChar
import org.jyutping.jyutping.extensions.space
import org.jyutping.jyutping.extensions.toneConverted
import org.jyutping.jyutping.keyboard.Candidate
import org.jyutping.jyutping.keyboard.Engine
import org.jyutping.jyutping.keyboard.InputMethodMode
import org.jyutping.jyutping.keyboard.KeyboardCase
import org.jyutping.jyutping.keyboard.KeyboardForm
import org.jyutping.jyutping.keyboard.Pinyin
import org.jyutping.jyutping.keyboard.PinyinSegmentor
import org.jyutping.jyutping.keyboard.Segmentor
import org.jyutping.jyutping.keyboard.Structure
import org.jyutping.jyutping.keyboard.length
import org.jyutping.jyutping.keyboard.transformed
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.DatabasePreparer

class JyutpingInputMethodService: LifecycleInputMethodService(),
        ViewModelStoreOwner,
        SavedStateRegistryOwner {

        override fun onCreateInputView(): View {
                val view = ComposeKeyboardView(this)
                window?.window?.navigationBarColor = Color.keyboardLightBackground.toArgb()
                window?.window?.decorView?.let { decorView ->
                        decorView.setViewTreeLifecycleOwner(this)
                        decorView.setViewTreeViewModelStoreOwner(this)
                        decorView.setViewTreeSavedStateRegistryOwner(this)
                }
                return view
        }

        override fun onCreate() {
                super.onCreate()
                savedStateRegistryController.performRestore(null)
                DatabasePreparer.prepare(this)
        }

        override val viewModelStore: ViewModelStore
                get() = store
        override val lifecycle: Lifecycle
                get() = dispatcher.lifecycle

        private val store = ViewModelStore()

        private val savedStateRegistryController = SavedStateRegistryController.create(this)

        override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

        val inputMethodMode: MutableState<InputMethodMode> = mutableStateOf(InputMethodMode.Cantonese)
        fun toggleInputMethodMode() {
                val newMode: InputMethodMode = when (inputMethodMode.value) {
                        InputMethodMode.Cantonese -> InputMethodMode.ABC
                        InputMethodMode.ABC -> InputMethodMode.Cantonese
                }
                inputMethodMode.value = newMode
        }

        val keyboardForm: MutableState<KeyboardForm> = mutableStateOf(KeyboardForm.Alphabetic)
        fun transformTo(destination: KeyboardForm) {
                if (isBuffering.value) {
                        bufferText = String.empty
                }
                keyboardForm.value = destination
                adjustKeyboardCase()
        }

        val keyboardCase: MutableState<KeyboardCase> = mutableStateOf(KeyboardCase.Lowercased)
        private fun updateKeyboardCase(case: KeyboardCase) {
                keyboardCase.value = case
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

        val characterStandard: MutableState<CharacterStandard> = run {
                // TODO: Fetch from Config
                mutableStateOf(CharacterStandard.Traditional)
        }
        fun updateCharacterStandard(standard: CharacterStandard) {
                characterStandard.value = standard
                // TODO: Save to Config
        }
        val candidateState: MutableIntState = mutableIntStateOf(1)
        val candidates: MutableState<List<Candidate>> = mutableStateOf(listOf())
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
                                        }
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
                                                                val leadingLength: Int = bestScheme?.map { it.length }?.reduce { acc, i -> acc + i } ?: 0
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
                                                candidates.value = suggestions.map { it.transformed(characterStandard.value) }.distinct()
                                        }
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                                'v' -> {}
                                'x' -> {}
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
                                                candidates.value = suggestions.map { it.transformed(characterStandard.value) }.distinct()
                                        }
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                                else -> {
                                        val processingText: String = value.toneConverted()
                                        val segmentation = Segmentor.segment(processingText, db)
                                        val suggestions = Engine.suggest(text = processingText, segmentation = segmentation, db = db)
                                        val mark: String = run {
                                                val firstCandidate = suggestions.firstOrNull()
                                                if (firstCandidate != null && firstCandidate.input.length == processingText.length) firstCandidate.mark else processingText
                                        }
                                        currentInputConnection.setComposingText(mark, mark.length)
                                        candidates.value = suggestions.map { it.transformed(characterStandard.value) }.distinct()
                                        if (isBuffering.value.not()) {
                                                isBuffering.value = true
                                        }
                                }
                        }
                        candidateState.intValue += 1
                }
        val isBuffering: MutableState<Boolean> = mutableStateOf(false)
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
