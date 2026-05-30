package org.jyutping.jyutping.models

import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.presets.PresetString

typealias Scheme = List<Syllable>

/** Count of all input keys */
val Scheme.schemeLength: Int
        get() = this.map { it.alias.size }.fold(0) { acc, i -> acc + i}

val Scheme.aliasText: String
        get() = this.flatMap { it.alias }.joinToString(PresetString.EMPTY) { it.text }

val Scheme.originText: String
        get() = this.flatMap { it.origin }.joinToString(PresetString.EMPTY) { it.text }

val Scheme.aliasAnchors: List<VirtualInputKey>
        get() = this.mapNotNull { it.alias.firstOrNull() }

val Scheme.originAnchors: List<VirtualInputKey>
        get() = this.mapNotNull { it.origin.firstOrNull() }

val Scheme.aliasAnchorsText: String
        get() = this.mapNotNull { it.alias.firstOrNull()?.text }.joinToString(PresetString.EMPTY)

val Scheme.originAnchorsText: String
        get() = this.mapNotNull { it.origin.firstOrNull()?.text }.joinToString(PresetString.EMPTY)

val Scheme.mark: String
        get() = this.joinToString(PresetString.SPACE) { it.aliasText }

val Scheme.syllableText: String
        get() = this.joinToString(PresetString.SPACE) { it.originText }

// REASON: *am => [*aa, m] => *aam
fun Scheme.isValid(): Boolean {
        if (this.size < 2) return true
        val shouldContinue = this.dropLast(1).any { it.origin.lastOrNull() === VirtualInputKey.letterA }
        if (shouldContinue.negative) return true
        val originCount = this.longAEndingCount { it.originCode }
        if (originCount < 1) return true
        return originCount == this.longAEndingCount { it.aliasCode }
}
private fun Scheme.longAEndingCount(selector: (Syllable) -> Long): Int {
        val letterACode = VirtualInputKey.letterA.code
        val letterGCode = VirtualInputKey.letterG.code
        val letterMCode = VirtualInputKey.letterM.code
        val letterNCode = VirtualInputKey.letterN.code
        var count = 0
        var thirdLastCode = 0
        var secondLastCode = 0
        var lastCode = 0
        for (syllable in this) {
                var syllableCode = selector(syllable)
                var divisor = 1L
                while ((syllableCode / divisor) >= 100L) {
                        divisor *= 100L
                }
                while (divisor > 0L) {
                        val keyCode = (syllableCode / divisor).toInt()
                        if (secondLastCode == letterACode && lastCode == letterACode && keyCode == letterMCode) {
                                count += 1
                        } else if (thirdLastCode == letterACode && secondLastCode == letterACode && lastCode == letterNCode && keyCode == letterGCode) {
                                count += 1
                        }
                        syllableCode %= divisor
                        divisor /= 100L
                        thirdLastCode = secondLastCode
                        secondLastCode = lastCode
                        lastCode = keyCode
                }
        }
        return count
}
