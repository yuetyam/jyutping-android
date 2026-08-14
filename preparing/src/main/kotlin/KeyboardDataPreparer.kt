package org.jyutping.preparing

import java.sql.Connection

object KeyboardDataPreparer {
        fun prepare(connection: Connection) {
                prepareCoreLexiconTable(connection)
                prepareStructureTable(connection)
                preparePinyinTable(connection)
                prepareCangjieTable(connection)
                prepareQuickTable(connection)
                prepareStrokeTable(connection)
                prepareSymbolTable(connection)
                prepareEmojiSkinMapTable(connection)
                preparePlainTextTable(connection)
                prepareCoreSyllableTable(connection)
                prepareNineKeySyllableTable(connection)
                preparePinyinSyllableTable(connection)
                prepareCharacterVariantTable(connection, "CharacterVariant.AncientBooksPublishing.txt", "variant_abp")
                prepareCharacterVariantTable(connection, "CharacterVariant.HongKong.txt", "variant_hk")
                prepareCharacterVariantTable(connection, "CharacterVariant.Inherited.txt", "variant_old")
                prepareCharacterVariantTable(connection, "CharacterVariant.PRCGeneral.txt", "variant_prc")
                prepareCharacterVariantTable(connection, "CharacterVariant.Simplified.txt", "variant_sim")
                prepareCharacterVariantTable(connection, "CharacterVariant.Taiwan.txt", "variant_tw")
                createIndexes(connection)
        }

        private fun prepareCoreLexiconTable(connection: Connection) {
                connection.execute("CREATE TABLE lexicon_core (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, romanization TEXT NOT NULL, char_count INTEGER NOT NULL, complexity INTEGER NOT NULL, anchors INTEGER NOT NULL, spell INTEGER NOT NULL, anchors_9key INTEGER NOT NULL, spell_9key INTEGER NOT NULL);")
                val entries = LexiconConverter.jyutping()
                batchInsert(connection, "INSERT INTO lexicon_core (word, romanization, char_count, complexity, anchors, spell, anchors_9key, spell_9key) VALUES (?, ?, ?, ?, ?, ?, ?, ?);", entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.romanization)
                        statement.setInt(3, entry.charCount)
                        statement.setLong(4, entry.complexity)
                        statement.setLong(5, entry.anchors)
                        statement.setLong(6, entry.spell)
                        statement.setLong(7, entry.nineKeyAnchors)
                        statement.setLong(8, entry.nineKeySpell)
                }
        }

        private fun prepareStructureTable(connection: Connection) {
                connection.execute("CREATE TABLE structure_table (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, romanization TEXT NOT NULL, char_count INTEGER NOT NULL, complexity INTEGER NOT NULL, spell INTEGER NOT NULL, spell_9key INTEGER NOT NULL);")
                val entries = LexiconConverter.structure()
                batchInsert(connection, "INSERT INTO structure_table (word, romanization, char_count, complexity, spell, spell_9key) VALUES (?, ?, ?, ?, ?, ?);", entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.romanization)
                        statement.setInt(3, entry.charCount)
                        statement.setLong(4, entry.complexity)
                        statement.setLong(5, entry.spell)
                        statement.setLong(6, entry.nineKeySpell)
                }
        }

        private fun preparePinyinTable(connection: Connection) {
                connection.execute("CREATE TABLE pinyin_lexicon (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, romanization TEXT NOT NULL, char_count INTEGER NOT NULL, complexity INTEGER NOT NULL, anchors INTEGER NOT NULL, spell INTEGER NOT NULL, anchors_9key INTEGER NOT NULL, spell_9key INTEGER NOT NULL);")
                val entries = LexiconConverter.pinyin()
                batchInsert(connection, "INSERT INTO pinyin_lexicon (word, romanization, char_count, complexity, anchors, spell, anchors_9key, spell_9key) VALUES (?, ?, ?, ?, ?, ?, ?, ?);", entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.romanization)
                        statement.setInt(3, entry.charCount)
                        statement.setLong(4, entry.complexity)
                        statement.setLong(5, entry.anchors)
                        statement.setLong(6, entry.spell)
                        statement.setLong(7, entry.nineKeyAnchors)
                        statement.setLong(8, entry.nineKeySpell)
                }
        }

        private fun prepareCangjieTable(connection: Connection) {
                connection.execute("CREATE TABLE cangjie_table (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, cangjie5 TEXT NOT NULL, c5complex INTEGER NOT NULL, c5code INTEGER NOT NULL, cangjie3 TEXT NOT NULL, c3complex INTEGER NOT NULL, c3code INTEGER NOT NULL);")
                val entries = Cangjie.generate()
                batchInsert(connection, "INSERT INTO cangjie_table (word, cangjie5, c5complex, c5code, cangjie3, c3complex, c3code) VALUES (?, ?, ?, ?, ?, ?, ?);", entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.cangjie5)
                        statement.setInt(3, entry.c5complex)
                        statement.setLong(4, entry.c5code)
                        statement.setString(5, entry.cangjie3)
                        statement.setInt(6, entry.c3complex)
                        statement.setLong(7, entry.c3code)
                }
        }

        private fun prepareQuickTable(connection: Connection) {
                connection.execute("CREATE TABLE quick_table (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, quick5 TEXT NOT NULL, q5complex INTEGER NOT NULL, q5code INTEGER NOT NULL, quick3 TEXT NOT NULL, q3complex INTEGER NOT NULL, q3code INTEGER NOT NULL);")
                val entries = Quick.generate()
                batchInsert(connection, "INSERT INTO quick_table (word, quick5, q5complex, q5code, quick3, q3complex, q3code) VALUES (?, ?, ?, ?, ?, ?, ?);", entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.quick5)
                        statement.setInt(3, entry.q5complex)
                        statement.setLong(4, entry.q5code)
                        statement.setString(5, entry.quick3)
                        statement.setInt(6, entry.q3complex)
                        statement.setLong(7, entry.q3code)
                }
        }

        private fun prepareStrokeTable(connection: Connection) {
                connection.execute("CREATE TABLE stroke_table (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, stroke TEXT NOT NULL, complex INTEGER NOT NULL, code INTEGER NOT NULL);")
                val entries = Stroke.generate()
                batchInsert(connection, "INSERT INTO stroke_table (word, stroke, complex, code) VALUES (?, ?, ?, ?);", entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.stroke)
                        statement.setInt(3, entry.complex)
                        statement.setLong(4, entry.code)
                }
        }

        private fun prepareSymbolTable(connection: Connection) {
                connection.execute("CREATE TABLE symbol_table (id INTEGER PRIMARY KEY AUTOINCREMENT, category INTEGER NOT NULL, unicode_version INTEGER NOT NULL, code_point TEXT NOT NULL, cantonese TEXT NOT NULL, romanization TEXT NOT NULL, complexity INTEGER NOT NULL, spell INTEGER NOT NULL, spell_9key INTEGER NOT NULL);")
                val entries = readResourceLines("symbol.txt").mapNotNull { line ->
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 5) return@mapNotNull null
                        val romanization = parts[4]
                        val complexity = romanization.split(PresetString.SPACE).map { it.length - 1 }.decimalOverflowed()
                        val letters = romanization.filter(Char::isLowercaseBasicLatinLetter)
                        SymbolEntry(
                                category = parts[0].toInt(),
                                unicodeVersion = parts[1].toInt(),
                                codePoint = parts[2],
                                cantonese = parts[3],
                                romanization = romanization,
                                complexity = complexity,
                                spell = letters.serialCode,
                                nineKeySpell = letters.keypadCode,
                        )
                }
                batchInsert(connection, "INSERT INTO symbol_table (category, unicode_version, code_point, cantonese, romanization, complexity, spell, spell_9key) VALUES (?, ?, ?, ?, ?, ?, ?, ?);", entries) { statement, entry ->
                        statement.setInt(1, entry.category)
                        statement.setInt(2, entry.unicodeVersion)
                        statement.setString(3, entry.codePoint)
                        statement.setString(4, entry.cantonese)
                        statement.setString(5, entry.romanization)
                        statement.setLong(6, entry.complexity)
                        statement.setLong(7, entry.spell)
                        statement.setLong(8, entry.nineKeySpell)
                }
        }

        private fun prepareEmojiSkinMapTable(connection: Connection) {
                connection.execute("CREATE TABLE emoji_skin_map (id INTEGER PRIMARY KEY AUTOINCREMENT, source TEXT NOT NULL, target TEXT NOT NULL);")
                val entries = readResourceLines("skin-tone-map.txt").mapNotNull { line ->
                        val parts = line.split(PresetString.TAB)
                        if (parts.size == 2) parts[0] to parts[1] else null
                }
                batchInsert(connection, "INSERT INTO emoji_skin_map (source, target) VALUES (?, ?);", entries) { statement, entry ->
                        statement.setString(1, entry.first)
                        statement.setString(2, entry.second)
                }
        }

        private fun preparePlainTextTable(connection: Connection) {
                connection.execute("CREATE TABLE plain_text_table (id INTEGER PRIMARY KEY AUTOINCREMENT, input TEXT NOT NULL, word TEXT NOT NULL, letter_count INTEGER NOT NULL, spell INTEGER NOT NULL, spell_9key INTEGER NOT NULL);")
                val entries = PlainText.convert()
                batchInsert(connection, "INSERT INTO plain_text_table (input, word, letter_count, spell, spell_9key) VALUES (?, ?, ?, ?, ?);", entries) { statement, entry ->
                        statement.setString(1, entry.input)
                        statement.setString(2, entry.word)
                        statement.setInt(3, entry.letterCount)
                        statement.setLong(4, entry.spell)
                        statement.setLong(5, entry.nineKeySpell)
                }
        }

        private fun prepareCoreSyllableTable(connection: Connection) {
                connection.execute("CREATE TABLE syllable_core_table (alias_code INTEGER PRIMARY KEY, origin_code INTEGER NOT NULL, alias TEXT NOT NULL, origin TEXT NOT NULL);")
                val entries = syllablePairs("syllable-core.txt")
                batchInsert(connection, "INSERT INTO syllable_core_table (alias_code, origin_code, alias, origin) VALUES (?, ?, ?, ?);", entries) { statement, entry ->
                        statement.setLong(1, entry.first.serialCode)
                        statement.setLong(2, entry.second.serialCode)
                        statement.setString(3, entry.first)
                        statement.setString(4, entry.second)
                }
        }

        private fun prepareNineKeySyllableTable(connection: Connection) {
                connection.execute("CREATE TABLE syllable_9key_table (alias_code INTEGER PRIMARY KEY, origin_code INTEGER NOT NULL, alias_9key_code INTEGER NOT NULL, origin_9key_code INTEGER NOT NULL, alias TEXT NOT NULL, origin TEXT NOT NULL);")
                val entries = syllablePairs("syllable-9key.txt")
                batchInsert(connection, "INSERT INTO syllable_9key_table (alias_code, origin_code, alias_9key_code, origin_9key_code, alias, origin) VALUES (?, ?, ?, ?, ?, ?);", entries) { statement, entry ->
                        statement.setLong(1, entry.first.serialCode)
                        statement.setLong(2, entry.second.serialCode)
                        statement.setLong(3, entry.first.keypadCode)
                        statement.setLong(4, entry.second.keypadCode)
                        statement.setString(5, entry.first)
                        statement.setString(6, entry.second)
                }
        }

        private fun preparePinyinSyllableTable(connection: Connection) {
                connection.execute("CREATE TABLE syllable_pinyin_table (code INTEGER PRIMARY KEY, code_9key INTEGER NOT NULL, syllable TEXT NOT NULL);")
                val entries = readResourceLines("syllable-pinyin.txt").map(String::trim).filter(String::isNotEmpty)
                batchInsert(connection, "INSERT INTO syllable_pinyin_table (code, code_9key, syllable) VALUES (?, ?, ?);", entries) { statement, syllable ->
                        statement.setLong(1, syllable.serialCode)
                        statement.setLong(2, syllable.keypadCode)
                        statement.setString(3, syllable)
                }
        }

        private fun prepareCharacterVariantTable(connection: Connection, fileName: String, tableName: String) {
                connection.execute("CREATE TABLE $tableName (source INTEGER PRIMARY KEY, target INTEGER NOT NULL);")
                val entries = CharacterVariant.generate(fileName)
                batchInsert(connection, "INSERT INTO $tableName (source, target) VALUES (?, ?);", entries) { statement, entry ->
                        statement.setInt(1, entry.left)
                        statement.setInt(2, entry.right)
                }
        }

        private fun syllablePairs(fileName: String): List<Pair<String, String>> {
                return readResourceLines(fileName).map(String::trim).filter(String::isNotEmpty).map { line ->
                        val parts = line.split(PresetString.TAB)
                        require(parts.size == 2) { "$fileName: bad format: $line" }
                        parts[0] to parts[1]
                }
        }

        private fun createIndexes(connection: Connection) {
                val commands = listOf(
                        "CREATE INDEX ix_lexicon_core_anchors ON lexicon_core (anchors, char_count);",
                        "CREATE INDEX ix_lexicon_core_spell ON lexicon_core (spell, complexity);",
                        "CREATE INDEX ix_lexicon_core_anchors_9key ON lexicon_core (anchors_9key, char_count);",
                        "CREATE INDEX ix_lexicon_core_spell_9key ON lexicon_core (spell_9key, complexity);",
                        "CREATE INDEX ix_lexicon_core_word ON lexicon_core (word);",
                        "CREATE INDEX ix_structure_spell ON structure_table (spell, complexity);",
                        "CREATE INDEX ix_structure_spell_9key ON structure_table (spell_9key, complexity);",
                        "CREATE INDEX ix_pinyin_anchors ON pinyin_lexicon (anchors, char_count);",
                        "CREATE INDEX ix_pinyin_spell ON pinyin_lexicon (spell, complexity);",
                        "CREATE INDEX ix_pinyin_anchors_9key ON pinyin_lexicon (anchors_9key, char_count);",
                        "CREATE INDEX ix_pinyin_spell_9key ON pinyin_lexicon (spell_9key, complexity);",
                        "CREATE INDEX ix_cangjie_cangjie5 ON cangjie_table (cangjie5, c5complex);",
                        "CREATE INDEX ix_cangjie_c5code ON cangjie_table (c5code);",
                        "CREATE INDEX ix_cangjie_cangjie3 ON cangjie_table (cangjie3, c3complex);",
                        "CREATE INDEX ix_cangjie_c3code ON cangjie_table (c3code);",
                        "CREATE INDEX ix_quick_quick5 ON quick_table (quick5, q5complex);",
                        "CREATE INDEX ix_quick_q5code ON quick_table (q5code);",
                        "CREATE INDEX ix_quick_quick3 ON quick_table (quick3, q3complex);",
                        "CREATE INDEX ix_quick_q3code ON quick_table (q3code);",
                        "CREATE INDEX ix_stroke_stroke ON stroke_table (stroke, complex);",
                        "CREATE INDEX ix_stroke_code ON stroke_table (code, complex);",
                        "CREATE INDEX ix_symbol_spell ON symbol_table (spell, complexity);",
                        "CREATE INDEX ix_symbol_spell_9key ON symbol_table (spell_9key, complexity);",
                        "CREATE INDEX ix_emoji_skin_map_source ON emoji_skin_map (source);",
                        "CREATE INDEX ix_plain_text_spell ON plain_text_table (spell, letter_count);",
                        "CREATE INDEX ix_plain_text_spell_9key ON plain_text_table (spell_9key, letter_count);",
                )
                commands.forEach(connection::execute)
        }

        private data class SymbolEntry(
                val category: Int,
                val unicodeVersion: Int,
                val codePoint: String,
                val cantonese: String,
                val romanization: String,
                val complexity: Long,
                val spell: Long,
                val nineKeySpell: Long,
        )
}

fun Connection.execute(command: String) {
        createStatement().use { it.executeUpdate(command) }
}
