package org.jyutping.jyutping.models

import org.jyutping.jyutping.presets.PresetCharacter

data class BasicInputEvent(
        val key: VirtualInputKey,
        val case: KeyboardCase,
) {
        /** Is not lowercased */
        val isCapitalized: Boolean
                get() = case.isCapitalized
}

fun List<BasicInputEvent>.previewMarkNormalized(): String = buildString {
        val inputLength = this@previewMarkNormalized.size
        var index = 0
        while (index < inputLength) {
                val event = this@previewMarkNormalized[index]
                val isPaired = (index + 1 < inputLength) && (event.key == this@previewMarkNormalized[index + 1].key)
                if (isPaired) {
                        val replacement = when (event.key) {
                                VirtualInputKey.letterV -> VirtualInputKey.number4
                                VirtualInputKey.letterX -> VirtualInputKey.number5
                                VirtualInputKey.letterQ -> VirtualInputKey.number6
                                else -> null
                        }
                        if (replacement != null) {
                                append(replacement.character)
                                index += 2
                                if (index < inputLength) {
                                        append(PresetCharacter.SPACE)
                                }
                                continue
                        }
                }
                val matched = when (event.key) {
                        VirtualInputKey.letterV -> VirtualInputKey.number1
                        VirtualInputKey.letterX -> VirtualInputKey.number2
                        VirtualInputKey.letterQ -> VirtualInputKey.number3
                        else -> if (event.key.isLetter) null else event.key
                }
                index += 1
                if (matched != null) {
                        append(matched.character)
                        if (index < inputLength) {
                                append(PresetCharacter.SPACE)
                        }
                } else {
                        val keyText = if (event.isCapitalized) event.key.text.uppercase() else event.key.text
                        append(keyText)
                }
        }
}
