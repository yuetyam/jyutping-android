package org.jyutping.jyutping.models

import org.jyutping.jyutping.presets.PresetString

typealias Scheme = List<Syllable>
typealias Segmentation = List<Scheme>

/** Count of all alias input keys */
val Scheme.length: Int
        get() = this.fold(0) { acc, syllable -> acc + syllable.alias.size }

/**
 * Conjoined digit of syllable origin lengths.
 *
 * For example: lengths of syllables “gwong dung dou” are `[5, 4, 3]`, which makes the `complexity` become `543`
 */
val Scheme.complexity: Long
        get() = this.map { it.origin.size }.decimalOverflowed()

/** Origin keys conjoined as sequence */
val Scheme.originKeys: List<VirtualInputKey>
        get() = this.flatMap { it.origin }

/** Alias texts conjoined as one text */
val Scheme.aliasText: String
        get() = this.flatMap { it.alias }.joinToString(PresetString.EMPTY) { it.text }

/** Origin texts conjoined as one text */
val Scheme.originText: String
        get() = this.flatMap { it.origin }.joinToString(PresetString.EMPTY) { it.text }

/** Anchors of alias input keys */
val Scheme.aliasAnchors: List<VirtualInputKey>
        get() = this.mapNotNull { it.alias.firstOrNull() }

/** Anchors of origin input keys */
val Scheme.originAnchors: List<VirtualInputKey>
        get() = this.mapNotNull { it.origin.firstOrNull() }

/** Anchors of alias input key texts, conjoined as one text */
val Scheme.aliasAnchorsText: String
        get() = this.mapNotNull { it.alias.firstOrNull()?.text }.joinToString(PresetString.EMPTY)

/** Anchors of origin input key texts, conjoined as one text */
val Scheme.originAnchorsText: String
        get() = this.mapNotNull { it.origin.firstOrNull()?.text }.joinToString(PresetString.EMPTY)

/** Alias texts as syllables */
val Scheme.mark: String
        get() = this.joinToString(separator = PresetString.SPACE) { it.aliasText }

/** Origin texts as syllables */
val Scheme.syllableText: String
        get() = this.joinToString(separator = PresetString.SPACE) { it.originText }
