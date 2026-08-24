package org.jyutping.jyutping.models

import org.jyutping.jyutping.presets.PresetString

typealias Scheme = List<Syllable>
typealias Segmentation = List<Scheme>

/** Count of all alias input keys */
val Scheme.schemeLength: Int
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

/** Anchors of alias input keys */
val Scheme.aliasAnchors: List<VirtualInputKey>
        get() = this.mapNotNull { it.alias.firstOrNull() }

/** Alias texts as syllables */
val Scheme.previewMark: String
        get() = this.joinToString(separator = PresetString.SPACE) { it.aliasText }

/** Origin texts as syllables */
val Scheme.syllableText: String
        get() = this.joinToString(separator = PresetString.SPACE) { it.originText }
