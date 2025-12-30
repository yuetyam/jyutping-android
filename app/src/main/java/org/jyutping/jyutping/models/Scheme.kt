package org.jyutping.jyutping.models

import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.presets.PresetString

typealias Scheme = List<Syllable>

/** Count of all input events */
val Scheme.length: Int
        get() = this.map { it.alias.size }.fold(0) { acc, i -> acc + i}

val Scheme.aliasText: String
        get() = this.flatMap { it.alias }.joinToString(PresetString.EMPTY) { it.text }

val Scheme.originText: String
        get() = this.flatMap { it.origin }.joinToString(PresetString.EMPTY) { it.text }

val Scheme.aliasAnchors: List<InputKeyEvent>
        get() = this.mapNotNull { it.alias.firstOrNull() }

val Scheme.originAnchors: List<InputKeyEvent>
        get() = this.mapNotNull { it.origin.firstOrNull() }

val Scheme.aliasAnchorsText: String
        get() = this.mapNotNull { it.alias.firstOrNull()?.text }.joinToString(PresetString.EMPTY)

val Scheme.originAnchorsText: String
        get() = this.mapNotNull { it.origin.firstOrNull()?.text }.joinToString(PresetString.EMPTY)

val Scheme.mark: String
        get() = this.joinToString(PresetString.SPACE) { it.aliasText }

val Scheme.syllableText: String
        get() = this.joinToString(PresetString.SPACE) { it.originText }

/*
extension RandomAccessCollection where Element == Syllable {

        /// Count of all alias events
        public var length: Int {
                return map(\.alias.count).summation
        }

        /// Alias texts conjoined as one text
        public var aliasText: String {
                return flatMap(\.alias).map(\.text).joined()
        }

        /// Origin texts conjoined as one text
        public var originText: String {
                return flatMap(\.origin).map(\.text).joined()
        }

        /// Anchors of alias events
        public var aliasAnchors: [InputEvent] {
                return compactMap(\.alias.first)
        }

        /// Anchors of origin events
        public var originAnchors: [InputEvent] {
                return compactMap(\.origin.first)
        }

        /// Anchors of alias event texts, conjoined as one text
        public var aliasAnchorsText: String {
                return compactMap(\.alias.first?.text).joined()
        }

        /// Anchors of origin event texts, conjoined as one text
        public var originAnchorsText: String {
                return compactMap(\.origin.first?.text).joined()
        }

        /// Alias texts as syllables
        public var mark: String {
                return map(\.aliasText).joined(separator: String.space)
        }

        /// Origin texts as syllables
        public var syllableText: String {
                return map(\.originText).joined(separator: String.space)
        }
}
*/

fun Scheme.isValid(): Boolean {
        if (this.size < 2) return true
        val shouldContinue = this.dropLast(1).any { it.origin.lastOrNull() === InputKeyEvent.letterA }
        if (shouldContinue.negative) return true
        val regex = Regex("aa(m|ng)")
        val originNumber = regex.findAll(this.originText).count()
        if (originNumber < 1) return true
        val tokenNumber = regex.findAll(this.aliasText).count()
        if (tokenNumber < 1) return false
        return originNumber == tokenNumber
}
