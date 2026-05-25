package org.jyutping.jyutping.models

import android.database.sqlite.SQLiteStatement
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
                db: DatabaseHelper,
                sessionState: Long
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
                return transformed(lexicons = chained, commentForm = commentForm, charset = charset, db = db, sessionState = sessionState)
        }

        fun transformed(lexicons: List<Lexicon>, commentForm: RomanizationForm, charset: CharacterStandard, db: DatabaseHelper, sessionState: Long): List<Candidate> {
                when (charset) {
                        CharacterStandard.Preset, CharacterStandard.Custom, CharacterStandard.Etymology, CharacterStandard.OpenCC -> {
                                return lexicons.map { Candidate(lexicon = it, commentForm = commentForm, sessionState = sessionState) }.distinct()
                        }
                        CharacterStandard.Inherited, CharacterStandard.HongKong, CharacterStandard.Taiwan, CharacterStandard.AncientBooksPublishing -> {
                                val command = "SELECT IFNULL((SELECT target FROM ${charset.variantTableName} WHERE source = ? LIMIT 1), 0) AS code_point;"
                                val statement = db.readableDatabase.compileStatement(command)
                                val entries = lexicons.map { lexicon ->
                                        if (lexicon.isNotCantonese) return@map Candidate(lexicon = lexicon, commentForm = commentForm, sessionState = sessionState)
                                        val codes = lexicon.text.codePoints().map { variantMatch(it, statement) }
                                        val convertedText = buildString { codes.forEachOrdered { appendCodePoint(it) } }
                                        return@map Candidate(text = convertedText, lexicon = lexicon, commentForm = commentForm, sessionState = sessionState)
                                }.distinct()
                                statement.close()
                                return entries
                        }
                        CharacterStandard.PrcGeneral -> {
                                val command = "SELECT IFNULL((SELECT target FROM ${charset.variantTableName} WHERE source = ? LIMIT 1), 0) AS code_point;"
                                val statement = db.readableDatabase.compileStatement(command)
                                val entries = TailoredConverter.transformed(lexicons = lexicons, commentForm = commentForm, sessionState = sessionState, statement = statement)
                                statement.close()
                                return entries
                        }
                        CharacterStandard.Mutilated -> {
                                val command = "SELECT IFNULL((SELECT target FROM ${charset.variantTableName} WHERE source = ? LIMIT 1), 0) AS code_point;"
                                val statement = db.readableDatabase.compileStatement(command)
                                val entries = Simplifier.transformed(lexicons = lexicons, commentForm = commentForm, sessionState = sessionState, statement = statement)
                                statement.close()
                                return entries
                        }
                }
        }
        private fun variantMatch(code: Int, statement: SQLiteStatement): Int {
                statement.clearBindings()
                statement.bindLong(1, code.toLong())
                val target = statement.simpleQueryForLong()
                return if (target < 1) code else target.toInt()
        }
}

private val CharacterStandard.variantTableName: String
        get() = when (this) {
                CharacterStandard.Inherited -> "variant_old"
                CharacterStandard.HongKong -> "variant_hk"
                CharacterStandard.Taiwan -> "variant_tw"
                CharacterStandard.PrcGeneral -> "variant_prc"
                CharacterStandard.AncientBooksPublishing -> "variant_abp"
                CharacterStandard.Mutilated -> "variant_sim"
                else -> "variant_hk"
        }
