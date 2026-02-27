package org.jyutping.jyutping.models

import org.jyutping.jyutping.CharacterStandard
import org.jyutping.jyutping.utilities.DatabaseHelper

object Converter {
        fun dispatch(
                memory: List<Lexicon>,
                defined: List<Lexicon>,
                marks: List<Lexicon>,
                symbols: List<Lexicon>,
                queried: List<Lexicon>,
                commentForm: RomanizationForm,
                charset: CharacterStandard,
                db: DatabaseHelper? = null
        ): List<Candidate> {
                val idealMemory = memory.filter { it.isIdealInputMemory }
                val notIdealMemory = memory.filter { it.isNotIdealInputMemory }
                val chained: MutableList<Lexicon> = if (idealMemory.isEmpty()) queried.toMutableList() else queried.filterNot { it.isCompound }.toMutableList()
                for (entry in notIdealMemory.reversed()) {
                        val index = chained.indexOfFirst { it.inputCount <= entry.inputCount }
                        if (index >= 0) {
                                chained.add(index = index, element = entry)
                        } else {
                                chained.add(entry)
                        }
                }
                chained.addAll(index = 0, elements = idealMemory.take(3) + defined + marks + idealMemory)
                for (symbol in symbols.reversed()) {
                        val index = chained.indexOfFirst { it.isCantonese && it.text == symbol.attached && it.romanization == symbol.romanization }
                        if (index >= 0) {
                                chained.add(index = index + 1, element = symbol)
                        }
                }
                return chained.map { Candidate(lexicon = it, commentForm = commentForm, charset = charset, db = db) }.distinct()
        }
}
