package org.jyutping.jyutping

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.MutableState
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
import org.jyutping.jyutping.extensions.keyboardLightBackground
import org.jyutping.jyutping.extensions.space
import org.jyutping.jyutping.keyboard.Candidate
import org.jyutping.jyutping.keyboard.KeyboardCase
import org.jyutping.jyutping.keyboard.KeyboardForm
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
        }

        override val viewModelStore: ViewModelStore
                get() = store
        override val lifecycle: Lifecycle
                get() = dispatcher.lifecycle

        private val store = ViewModelStore()

        private val savedStateRegistryController = SavedStateRegistryController.create(this)

        override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

        val keyboardForm: MutableState<KeyboardForm> = mutableStateOf(KeyboardForm.Alphabetic)
        fun transformTo(destination: KeyboardForm) {
                keyboardForm.value = destination
        }

        val keyboardCase: MutableState<KeyboardCase> = mutableStateOf(KeyboardCase.Lowercased)
        private fun updateKeyboardCaseTo(case: KeyboardCase) {
                keyboardCase.value = case
        }
        fun performShift() {
                val newCase: KeyboardCase = when (keyboardCase.value) {
                        KeyboardCase.Lowercased -> KeyboardCase.Uppercased
                        KeyboardCase.Uppercased -> KeyboardCase.Lowercased
                        KeyboardCase.CapsLocked -> KeyboardCase.Lowercased
                }
                updateKeyboardCaseTo(newCase)
        }

        val candidates: MutableState<List<Candidate>> = mutableStateOf(listOf())
        private val db by lazy { DatabaseHelper(this, DatabasePreparer.databaseName) }
        private var bufferText: String = ""
                set(value) {
                        field = value
                        if (value.isEmpty()) {
                                candidates.value = listOf()
                                currentInputConnection.setComposingText(value, value.length)
                                currentInputConnection.finishComposingText()
                                if (isBuffering.value) {
                                        isBuffering.value = false
                                }
                        } else {
                                currentInputConnection.setComposingText(value, value.length)
                                candidates.value = db.shortcut(value)
                                if (!isBuffering.value) {
                                        isBuffering.value = true
                                }
                        }
                }
        val isBuffering: MutableState<Boolean> = mutableStateOf(false)
        fun process(text: String) {
                bufferText += text
        }
        fun input(text: String) {
                currentInputConnection.commitText(text, text.length)
        }
        fun select(candidate: Candidate) {
                currentInputConnection.commitText(candidate.text, candidate.text.length)
                bufferText = bufferText.drop(candidate.input.length)
        }
        fun backspace() {
                if (bufferText.isEmpty()) {
                        currentInputConnection.deleteSurroundingText(1, 0)
                } else {
                        bufferText = bufferText.dropLast(1)
                }
        }
        fun performReturn() {
                if (bufferText.isEmpty()) {
                        sendDefaultEditorAction(true)
                } else {
                        currentInputConnection.commitText(bufferText, bufferText.length)
                        bufferText = ""
                }
        }
        fun space() {
                if (candidates.value.isEmpty()) {
                        currentInputConnection.commitText(String.space, String.space.length)
                } else {
                        select(candidates.value.first())
                }
        }
        fun dismissKeyboard() {
                requestHideSelf(InputMethodManager.HIDE_NOT_ALWAYS)
        }
        fun leftKey() {
                if (!isBuffering.value) {
                        val text = "，"
                        currentInputConnection.commitText(text, text.length)
                }
        }
        fun rightKey() {
                if (!isBuffering.value) {
                        val text = "。"
                        currentInputConnection.commitText(text, text.length)
                }
        }
}
