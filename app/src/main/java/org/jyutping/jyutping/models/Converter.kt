package org.jyutping.jyutping.models

import org.jyutping.jyutping.CharacterStandard
import org.jyutping.jyutping.keyboard.transformed
import org.jyutping.jyutping.utilities.DatabaseHelper

object Converter {
        fun dispatch(memory: List<Lexicon>, symbols: List<Lexicon>, queried: List<Lexicon>, standard: CharacterStandard, db: DatabaseHelper): List<Lexicon> {
                val hasMemory: Boolean = memory.isNotEmpty()
                val hasSymbols: Boolean = symbols.isNotEmpty()
                when {
                        hasMemory && hasSymbols -> {
                                val combined: MutableList<Lexicon> = (memory + queried).toMutableList()
                                for (symbol in symbols.reversed()) {
                                        val index = combined.indexOfFirst { it.lexiconText == symbol.lexiconText && it.romanization == symbol.romanization }
                                        if (index > -1) {
                                                combined.add(index = index + 1, element = symbol)
                                        }
                                }
                                return combined.map { it.transformed(standard, db) }.distinct()
                        }
                        hasMemory && !hasSymbols -> {
                                return (memory + queried).map { it.transformed(standard, db) }.distinct()
                        }
                        !hasMemory && hasSymbols -> {
                                val combined: MutableList<Lexicon> = queried.toMutableList()
                                for (symbol in symbols.reversed()) {
                                        val index = combined.indexOfFirst { it.lexiconText == symbol.lexiconText && it.romanization == symbol.romanization }
                                        if (index > -1) {
                                                combined.add(index = index + 1, element = symbol)
                                        }
                                }
                                return combined.map { it.transformed(standard, db) }.distinct()
                        }
                        else -> {
                                return queried.map { it.transformed(standard, db) }.distinct()
                        }
                }
        }
}
