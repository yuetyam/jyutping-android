package org.jyutping.jyutping.models

/** A single input carrying a definite key or a keypad letter group. */
sealed interface InputPulse {

        /** A definite virtual key and its keyboard case. */
        data class Basic(val event: BasicInputEvent) : InputPulse

        /** A nine-key keypad letter group without keyboard-case information. */
        data class Combined(val combo: KeypadCombo) : InputPulse

        /** Whether this pulse identifies a reverse-lookup trigger (`r`, `v`, `x`, or `q`), regardless of keyboard case. Keypad combos always return `false`. */
        val isReverseLookupTrigger: Boolean
                get() = when (this) {
                        is Basic -> event.key.isReverseLookupTrigger
                        is Combined -> false
                }

        /** Whether this pulse is a basic apostrophe event. */
        val isApostrophe: Boolean
                get() = when (this) {
                        is Basic -> event.key.isApostrophe
                        is Combined -> false
                }

        /** Display text of this pulse. Combos use their first letter. */
        val text: String
                get() = when (this) {
                        is Basic -> if (event.isCapitalized) event.key.text.uppercase() else event.key.text
                        is Combined -> combo.letters.first()
                }
}

/** Only the basic events, in list order. */
val List<InputPulse>.basicEvents: List<BasicInputEvent>
        get() = mapNotNull { (it as? InputPulse.Basic)?.event }

/** Only the combined keypad letter groups, in list order. */
val List<InputPulse>.keypadCombos: List<KeypadCombo>
        get() = mapNotNull { (it as? InputPulse.Combined)?.combo }
