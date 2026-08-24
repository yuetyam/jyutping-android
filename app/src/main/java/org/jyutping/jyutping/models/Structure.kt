package org.jyutping.jyutping.models

import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.extensions.isCantoneseToneDigit
import org.jyutping.jyutping.extensions.isSpace
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.extensions.strippedTones
import org.jyutping.jyutping.extensions.toneConverted
import org.jyutping.jyutping.presets.PresetCharacter
import org.jyutping.jyutping.presets.PresetString

object Structure {

        /**
         * Character Components Reverse Lookup. 拆字反查. 例如 木 + 木 = 林
         */
        fun reverseLookup(keys: List<VirtualInputKey>, segmentation: Segmentation): List<Lexicon> {
                val syllableKeys = keys.filter { it.isSyllableLetter }
                val searched = search(keys = syllableKeys, segmentation = segmentation)
                if (searched.isEmpty()) return emptyList()
                val inputText = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                val hasApostrophe: Boolean = keys.any { it.isApostrophe }
                val hasToneLetter: Boolean = keys.any { it.isToneLetter }
                return when {
                        hasApostrophe && hasToneLetter -> {
                                val text = inputText.toneConverted()
                                val textTones = text.filter { it.isCantoneseToneDigit }
                                if (textTones.length != 1) return emptyList()
                                val isToneInTail: Boolean = text.lastOrNull()?.isCantoneseToneDigit ?: false
                                val filtered = searched.filter { item ->
                                        val tones = item.romanization.filter { it.isCantoneseToneDigit }
                                        if (isToneInTail) tones.endsWith(textTones) else tones.startsWith(textTones)
                                }
                                filtered.flatMap { item -> Elephant.reveresLookup(text = item.text, input = inputText) }
                        }
                        hasApostrophe.negative && hasToneLetter -> {
                                val text = inputText.toneConverted()
                                val textTones = text.filter { it.isCantoneseToneDigit }
                                when (textTones.length) {
                                        1 -> {
                                                val isToneInTail: Boolean = text.lastOrNull()?.isCantoneseToneDigit ?: false
                                                val filtered = searched.filter { item ->
                                                        if (item.romanization.filterNot { it.isSpace }.startsWith(text)) {
                                                                true
                                                        } else {
                                                                val tones = item.romanization.filter { it.isCantoneseToneDigit }
                                                                if (isToneInTail) tones.endsWith(textTones) else tones.startsWith(textTones)
                                                        }
                                                }
                                                filtered.flatMap { item -> Elephant.reveresLookup(text = item.text, input = inputText) }
                                        }
                                        2 -> {
                                                val filtered = searched.filter { item ->
                                                        if (item.romanization.filterNot { it.isSpace }.startsWith(text)) {
                                                                true
                                                        } else {
                                                                textTones == item.romanization.filter { it.isCantoneseToneDigit }
                                                        }
                                                }
                                                filtered.flatMap { item -> Elephant.reveresLookup(text = item.text, input = inputText) }
                                        }
                                        else -> {
                                                val filtered = searched.filter { item ->
                                                        item.romanization.filterNot { it.isSpace }.startsWith(text)
                                                }
                                                filtered.flatMap { item -> Elephant.reveresLookup(text = item.text, input = inputText) }
                                        }
                                }
                        }
                        hasApostrophe && hasToneLetter.negative -> {
                                val textParts = inputText.split(PresetCharacter.APOSTROPHE).filter { it.isNotEmpty() }
                                val filtered = searched.filter { item ->
                                        val syllables = item.romanization.strippedTones().split(PresetCharacter.SPACE)
                                        syllables == textParts
                                }
                                filtered.flatMap { item -> Elephant.reveresLookup(text = item.text, input = inputText) }
                        }
                        else -> searched.flatMap { item -> Elephant.reveresLookup(text = item.text, input = inputText) }
                }
        }

        private fun search(keys: List<VirtualInputKey>, segmentation: Segmentation): List<Lexicon> {
                val inputLength: Int = keys.size
                val input: String = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                return segmentation.filter { it.schemeLength == inputLength }
                        .flatMap { scheme -> match(keys = scheme.originKeys, complexity = scheme.complexity, input = input) }
                        .distinct()
        }

        private fun match(keys: List<VirtualInputKey>, complexity: Long, input: String): List<Lexicon> {
                val spell: Long = keys.conjoinedCode
                val complexityValue: Long = complexity
                val items: MutableList<Lexicon> = mutableListOf()
                val command: String = "SELECT word, romanization FROM structure_table WHERE spell = $spell AND complexity = $complexityValue ORDER BY rowid LIMIT -1;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val word = cursor.getString(0)
                                val romanization = cursor.getString(1)
                                val instance = Lexicon(text = word, romanization = romanization, input = input)
                                items.add(instance)
                        }
                }
                return items
        }
}
