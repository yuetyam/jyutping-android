package org.jyutping.jyutping.models

import org.jyutping.jyutping.extensions.isCantoneseToneDigit
import org.jyutping.jyutping.extensions.isSpace
import org.jyutping.jyutping.extensions.toneConverted
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper

object Structure {
        fun reverseLookup(keys: List<VirtualInputKey>, segmentation: Segmentation, db: DatabaseHelper): List<Lexicon> {
                val markFreeText = keys.filter { it.isSyllableLetter }.joinToString(separator = PresetString.EMPTY) { it.text }
                val searched = search(text = markFreeText, segmentation = segmentation, db = db)
                if (searched.isEmpty()) return emptyList()
                val hasApostrophes = keys.any { it.isApostrophe }
                val hasTones = keys.any { it.isToneInputKey }
                val inputText = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                return when {
                        hasApostrophes && hasTones -> {
                                val text = inputText.toneConverted()
                                val textTones = text.filter { it.isCantoneseToneDigit }
                                if (textTones.length != 1) emptyList<Lexicon>()
                                val isToneInTail: Boolean = text.lastOrNull()?.isCantoneseToneDigit ?: false
                                val filtered = searched.filter { item ->
                                        val tones = item.romanization.filter { it.isCantoneseToneDigit }
                                        if (isToneInTail) tones.endsWith(textTones) else tones.startsWith(textTones)
                                }
                                filtered.flatMap { item ->
                                        db.reverseLookup(text = item.text)
                                                .map { romanization -> Lexicon(text = item.text, romanization = romanization, input = inputText) }
                                }
                        }
                        !hasApostrophes && hasTones -> {
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
                                                filtered.flatMap { item ->
                                                        db.reverseLookup(text = item.text)
                                                                .map { romanization -> Lexicon(text = item.text, romanization = romanization, input = inputText) }
                                                }
                                        }
                                        2 -> {
                                                val filtered = searched.filter { item ->
                                                        item.romanization.filterNot { it.isSpace }.startsWith(text) ||
                                                                item.romanization.filter { it.isCantoneseToneDigit } == textTones
                                                }
                                                filtered.flatMap { item ->
                                                        db.reverseLookup(text = item.text)
                                                                .map { romanization -> Lexicon(text = item.text, romanization = romanization, input = inputText) }
                                                }
                                        }
                                        else -> {
                                                val filtered = searched.filter { item ->
                                                        item.romanization.filterNot { it.isSpace }.startsWith(text)
                                                }
                                                filtered.flatMap { item ->
                                                        db.reverseLookup(text = item.text)
                                                                .map { romanization -> Lexicon(text = item.text, romanization = romanization, input = inputText) }
                                                }
                                        }
                                }
                        }
                        hasApostrophes && !hasTones -> {
                                val textParts = inputText.split(PresetString.APOSTROPHE)
                                val filtered = searched.filter { item ->
                                        val syllables = item.romanization.filterNot { it.isCantoneseToneDigit }.split(PresetString.SPACE)
                                        return@filter syllables == textParts
                                }
                                filtered.flatMap { item ->
                                        db.reverseLookup(text = item.text)
                                                .map { romanization -> Lexicon(text = item.text, romanization = romanization, input = inputText) }
                                }
                        }
                        else -> searched.flatMap { item ->
                                db.reverseLookup(text = item.text)
                                        .map { romanization -> Lexicon(text = item.text, romanization = romanization, input = inputText) }
                        }
                }
        }

        private fun search(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Lexicon> {
                val matched = db.structureMatch(text = text)
                val textLength = text.length
                val queried = segmentation.filter { it.length == textLength }.flatMap { db.structureMatch(text = it.originText) }
                return (matched + queried).distinct()
        }
}

private fun DatabaseHelper.structureMatch(text: String): List<Lexicon> {
        val instances: MutableList<Lexicon> = mutableListOf()
        val code = text.hashCode()
        val command = "SELECT word, romanization FROM structure_table WHERE spell = ${code};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val word = cursor.getString(0)
                val romanization = cursor.getString(1)
                val instance = Lexicon(text = word, romanization = romanization, input = text)
                instances.add(instance)
        }
        cursor.close()
        return instances
}
