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

fun Scheme.isValid(): Boolean {
        if (this.size < 2) return true
        val shouldContinue = this.dropLast(1).any { it.origin.lastOrNull() === VirtualInputKey.letterA }
        if (shouldContinue.negative) return true
        val regex = Regex("aa(m|ng)")
        val originNumber = regex.findAll(this.originText).count()
        if (originNumber < 1) return true
        val tokenNumber = regex.findAll(this.aliasText).count()
        if (tokenNumber < 1) return false
        return originNumber == tokenNumber
}
