package org.jyutping.jyutping

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import org.jyutping.jyutping.extensions.space
import org.jyutping.jyutping.keyboard.Candidate
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.DatabasePreparer

class JyutpingInputMethodService: LifecycleInputMethodService(),
        ViewModelStoreOwner,
        SavedStateRegistryOwner {

        override fun onCreateInputView(): View {
                val view = ComposeKeyboardView(this)
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

        var candidates: MutableState<List<Candidate>> = mutableStateOf(listOf())
        private val db by lazy { DatabaseHelper(this, DatabasePreparer.databaseName) }
        private var bufferText: String = ""
                set(value) {
                        field = value
                        if (value.isEmpty()) {
                                candidates.value = listOf()
                                currentInputConnection.finishComposingText()
                        } else {
                                currentInputConnection.setComposingText(value, value.length)
                                candidates.value = db.shortcut(value)
                        }
                }
        fun process(text: String) {
                bufferText += text
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
}
