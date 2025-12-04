package org.jyutping.preparing

import java.io.InputStream
import java.sql.DriverManager
import kotlin.use

object KeyboardDataPreparer {
        fun prepare(url: String) {
                createLexiconTable(url)
                createCharacterVariantTable(fileName = "CharacterVariant.AncientBooksPublishing.txt", tableName = "variant_abp", url = url)
                createCharacterVariantTable(fileName = "CharacterVariant.HongKong.txt", tableName = "variant_hk", url = url)
                createCharacterVariantTable(fileName = "CharacterVariant.Inherited.txt", tableName = "variant_old", url = url)
                createCharacterVariantTable(fileName = "CharacterVariant.PRCGeneral.txt", tableName = "variant_prc", url = url)
                createCharacterVariantTable(fileName = "CharacterVariant.Simplified.txt", tableName = "variant_sim", url = url)
                createCharacterVariantTable(fileName = "CharacterVariant.Taiwan.txt", tableName = "variant_tw", url = url)
                createStructureTable(url)
                createPinyinTable(url)
                createSyllablesTable(url)
                createPinyinSyllablesTable(url)
                createTextMarkTable(url)
                createSymbolTable(url)
                createEmojiSkinMapTable(url)
                createStrokeTable(url)
                createCangjieTable(url)
                createQuickTable(url)
                createIndexes(url)
        }
        private fun createIndexes(url: String) {
                val commands: List<String> = listOf(
                        "CREATE INDEX ix_core_lexicon_spell ON core_lexicon(spell);",
                        "CREATE INDEX ix_core_lexicon_anchors ON core_lexicon(anchors);",
                        "CREATE INDEX ix_core_lexicon_strict ON core_lexicon(spell, anchors);",
                        "CREATE INDEX ix_core_lexicon_nine_key_code ON core_lexicon(nine_key_code);",
                        "CREATE INDEX ix_core_lexicon_nine_key_anchors ON core_lexicon(nine_key_anchors);",
                        "CREATE INDEX ix_core_lexicon_word ON core_lexicon(word);",
                        "CREATE INDEX ix_core_lexicon_romanization ON core_lexicon(romanization);",

                        "CREATE INDEX ix_structure_spell ON structure_table(spell);",
                        "CREATE INDEX ix_structure_nine_key_code ON structure_table(nine_key_code);",

                        "CREATE INDEX ix_pinyin_spell ON pinyin_lexicon(spell);",
                        "CREATE INDEX ix_pinyin_anchors ON pinyin_lexicon(anchors);",
                        "CREATE INDEX ix_pinyin_strict ON pinyin_lexicon(spell, anchors);",
                        "CREATE INDEX ix_pinyin_nine_key_code ON pinyin_lexicon(nine_key_code);",
                        "CREATE INDEX ix_pinyin_nine_key_anchors ON pinyin_lexicon(nine_key_anchors);",

                        "CREATE INDEX ix_symbol_spell ON symbol_table(spell);",
                        "CREATE INDEX ix_symbol_nine_key_code ON symbol_table(nine_key_code);",
                        "CREATE INDEX ix_emoji_skin_map_source ON emoji_skin_map(source);",

                        "CREATE INDEX ix_mark_spell ON mark_table(spell);",
                        "CREATE INDEX ix_mark_code ON mark_table(code);",
                        "CREATE INDEX ix_mark_nine_key_code ON mark_table(nine_key_code);",

                        "CREATE INDEX ix_syllable_nine_key_alias_code ON syllable_table(nine_key_alias_code);",
                        "CREATE INDEX ix_pinyin_syllable_nine_key_code ON pinyin_syllable_table(nine_key_code);",

                        "CREATE INDEX ix_stroke_stroke ON stroke_table(stroke);",
                        "CREATE INDEX ix_stroke_spell ON stroke_table(spell);",
                        "CREATE INDEX ix_stroke_code ON stroke_table(code);",

                        "CREATE INDEX ix_cangjie_cangjie5 ON cangjie_table(cangjie5);",
                        "CREATE INDEX ix_cangjie_c5code ON cangjie_table(c5code);",
                        "CREATE INDEX ix_cangjie_cangjie3 ON cangjie_table(cangjie3);",
                        "CREATE INDEX ix_cangjie_c3code ON cangjie_table(c3code);",

                        "CREATE INDEX ix_quick_quick5 ON quick_table(quick5);",
                        "CREATE INDEX ix_quick_q5code ON quick_table(q5code);",
                        "CREATE INDEX ix_quick_quick3 ON quick_table(quick3);",
                        "CREATE INDEX ix_quick_q3code ON quick_table(q3code);",


                        "CREATE INDEX ix_variant_abp_left ON variant_abp(left);",
                        "CREATE INDEX ix_variant_abp_right ON variant_abp(right);",

                        "CREATE INDEX ix_variant_hk_left ON variant_hk(left);",
                        "CREATE INDEX ix_variant_hk_right ON variant_hk(right);",

                        "CREATE INDEX ix_variant_old_left ON variant_old(left);",
                        "CREATE INDEX ix_variant_old_right ON variant_old(right);",

                        "CREATE INDEX ix_variant_prc_left ON variant_prc(left);",
                        "CREATE INDEX ix_variant_prc_right ON variant_prc(right);",

                        "CREATE INDEX ix_variant_sim_left ON variant_sim(left);",
                        "CREATE INDEX ix_variant_sim_right ON variant_sim(right);",

                        "CREATE INDEX ix_variant_tw_left ON variant_tw(left);",
                        "CREATE INDEX ix_variant_tw_right ON variant_tw(right);",
                )
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        for (command in commands) {
                                statement.executeUpdate(command)
                        }
                }
                connection.close()
                println("Successfully created keyboard data indexes.")
        }

        private fun createLexiconTable(url: String) {
                val createTableCommand: String = "CREATE TABLE core_lexicon(word TEXT NOT NULL, romanization TEXT NOT NULL, anchors INTEGER NOT NULL, spell INTEGER NOT NULL, nine_key_anchors INTEGER NOT NULL, nine_key_code INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created lexicon table successfully.")
                val entries = LexiconConverter.jyutping()
                val insertEntryCommand: String = "INSERT INTO core_lexicon (word, romanization, anchors, spell, nine_key_anchors, nine_key_code) VALUES (?, ?, ?, ?, ?, ?);"
                println("Inserting ${entries.size} lexicon entries...")
                val inserted = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.romanization)
                        statement.setLong(3, entry.anchors)
                        statement.setLong(4, entry.spell)
                        statement.setLong(5, entry.nineKeyAnchors)
                        statement.setLong(6, entry.nineKeyCode)
                }
                connection.close()
                println("Inserted lexicon entries successfully: $inserted")
        }
        private fun createCharacterVariantTable(fileName: String, tableName: String, url: String) {
                val createTableCommand: String = "CREATE TABLE ${tableName}(left INTEGER NOT NULL, right INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created table $tableName successfully.")
                val entries = CharacterVariant.process(fileName)
                val insertEntryCommand: String = "INSERT INTO $tableName (left, right) VALUES (?, ?);"
                println("Inserting ${entries.size} $tableName entries...")
                val inserted = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setInt(1, entry.left)
                        statement.setInt(2, entry.right)
                }
                connection.close()
                println("Inserted $tableName entries successfully: $inserted")
        }
        private fun createStructureTable(url: String) {
                val createTableCommand: String = "CREATE TABLE structure_table(word TEXT NOT NULL, romanization TEXT NOT NULL, spell INTEGER NOT NULL, nine_key_code INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created structure table successfully.")
                val entries = LexiconConverter.structure()
                val insertEntryCommand: String = "INSERT INTO structure_table (word, romanization, spell, nine_key_code) VALUES (?, ?, ?, ?);"
                println("Inserting ${entries.size} structure entries...")
                val inserted = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.romanization)
                        statement.setLong(3, entry.spell)
                        statement.setLong(4, entry.nineKeyCode)
                }
                connection.close()
                println("Inserted structure entries successfully: $inserted")
        }

        private fun createPinyinTable(url: String) {
                val createTableCommand: String = "CREATE TABLE pinyin_lexicon(word TEXT NOT NULL, romanization TEXT NOT NULL, anchors INTEGER NOT NULL, spell INTEGER NOT NULL, nine_key_anchors INTEGER NOT NULL, nine_key_code INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created pinyin table successfully.")
                val entries = LexiconConverter.pinyin()
                val insertEntryCommand: String = "INSERT INTO pinyin_lexicon (word, romanization, anchors, spell, nine_key_anchors, nine_key_code) VALUES (?, ?, ?, ?, ?, ?);"
                println("Inserting ${entries.size} pinyin entries...")
                val insertedPinyin = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.romanization)
                        statement.setLong(3, entry.anchors)
                        statement.setLong(4, entry.spell)
                        statement.setLong(5, entry.nineKeyAnchors)
                        statement.setLong(6, entry.nineKeyCode)
                }
                connection.close()
                println("Inserted pinyin entries successfully: $insertedPinyin")
        }

        private fun createSyllablesTable(url: String) {
                val createTableCommand: String = "CREATE TABLE syllable_table(alias_code INTEGER NOT NULL PRIMARY KEY, origin_code INTEGER NOT NULL, nine_key_alias_code INTEGER NOT NULL, nine_key_origin_code INTEGER NOT NULL, alias TEXT NOT NULL, origin TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created syllable table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("syllable.txt") ?: error("Can not load syllable.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO syllable_table (alias_code, origin_code, nine_key_alias_code, nine_key_origin_code, alias, origin) VALUES (?, ?, ?, ?, ?, ?);"
                println("Inserting ${sourceLines.size} syllable entries...")
                val insertedSyllable = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 2) error(badLineFormat)
                        val alias = parts[0]
                        val origin = parts[1]
                        val aliasCode = alias.charCode
                        val originCode = origin.charCode
                        if (aliasCode == null || originCode == null) error(badLineFormat)
                        if (aliasCode == 0L || originCode == 0L) error(badLineFormat)
                        val nineKeyAliasCode = alias.nineKeyCharCode
                        val nineKeyOriginCode = origin.nineKeyCharCode
                        if (nineKeyAliasCode == null || nineKeyOriginCode == null) error(badLineFormat)
                        if (nineKeyAliasCode == 0L || nineKeyOriginCode == 0L) error(badLineFormat)
                        statement.setLong(1, aliasCode)
                        statement.setLong(2, originCode)
                        statement.setLong(3, nineKeyAliasCode)
                        statement.setLong(4, nineKeyOriginCode)
                        statement.setString(5, alias)
                        statement.setString(6, origin)
                }
                connection.close()
                println("Inserted syllable entries successfully: $insertedSyllable")
        }

        private fun createPinyinSyllablesTable(url: String) {
                val createTableCommand: String = "CREATE TABLE pinyin_syllable_table(code INTEGER NOT NULL PRIMARY KEY, nine_key_code INTEGER NOT NULL, syllable TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created pinyin syllable table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("pinyin-syllable.txt") ?: error("Can not load pinyin-syllable.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO pinyin_syllable_table (code, nine_key_code, syllable) VALUES (?, ?, ?);"
                println("Inserting ${sourceLines.size} pinyin-syllable entries...")
                val insertedPinyinSyll = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val code = line.charCode
                        val nineKeycode = line.nineKeyCharCode
                        if (code == null || nineKeycode == null) error(badLineFormat)
                        if (code == 0L || nineKeycode == 0L) error(badLineFormat)
                        statement.setLong(1, code)
                        statement.setLong(2, nineKeycode)
                        statement.setString(3, line)
                }
                connection.close()
                println("Inserted pinyin syllable entries successfully: $insertedPinyinSyll")
        }

        private fun createTextMarkTable(url: String) {
                val createTableCommand: String = "CREATE TABLE mark_table(input TEXT NOT NULL, mark TEXT NOT NULL, spell INTEGER NOT NULL, code INTEGER NOT NULL, nine_key_code INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created text mark table successfully.")
                val insertEntryCommand: String = "INSERT INTO mark_table (input, mark, spell, code, nine_key_code) VALUES (?, ?, ?, ?, ?);"
                val items = TextMarkLexicon.generate()
                println("Inserting ${items.size} text-mark entries...")
                val insertedMarks = batchInsert(connection, insertEntryCommand, items) { statement, item ->
                        statement.setString(1, item.input)
                        statement.setString(2, item.mark)
                        statement.setInt(3, item.spellCode)
                        statement.setLong(4, item.charCode)
                        statement.setLong(5, item.nineKeyCode)
                }
                connection.close()
                println("Inserted text mark entries successfully: $insertedMarks")
        }

        private fun createSymbolTable(url: String) {
                val createTableCommand: String = "CREATE TABLE symbol_table(category INTEGER NOT NULL, unicode_version INTEGER NOT NULL, code_point TEXT NOT NULL, cantonese TEXT NOT NULL, romanization TEXT NOT NULL, spell INTEGER NOT NULL, nine_key_code INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created symbol table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("symbol.txt") ?: error("Can not load symbol.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO symbol_table (category, unicode_version, code_point, cantonese, romanization, spell, nine_key_code) VALUES (?, ?, ?, ?, ?, ?, ?);"
                println("Inserting ${sourceLines.size} symbol entries...")
                val insertedSymbols = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 5) error(badLineFormat)
                        val category = parts[0].toIntOrNull() ?: error(badLineFormat)
                        val version = parts[1].toIntOrNull() ?: error(badLineFormat)
                        val codePoint = parts[2]
                        val cantonese = parts[3]
                        val romanization = parts[4]
                        val syllableText = romanization.filter { it.isLetter() }
                        val spellCode = syllableText.hashCode()
                        val nineKeyCode = syllableText.nineKeyCharCode ?: 0
                        statement.setInt(1, category)
                        statement.setInt(2, version)
                        statement.setString(3, codePoint)
                        statement.setString(4, cantonese)
                        statement.setString(5, romanization)
                        statement.setInt(6, spellCode)
                        statement.setLong(7, nineKeyCode)
                }
                connection.close()
                println("Inserted symbol entries successfully: $insertedSymbols")
        }
        private fun createEmojiSkinMapTable(url: String) {
                val createTableCommand: String = "CREATE TABLE emoji_skin_map(source TEXT NOT NULL, target TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created emoji-skin-map table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("skin-tone-map.txt") ?: error("Can not load skin-tone-map.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO emoji_skin_map (source, target) VALUES (?, ?);"
                println("Inserting ${sourceLines.size} emoji-skin-map entries...")
                val insertedEmoji = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 2) error(badLineFormat)
                        val source = parts[0]
                        val target = parts[1]
                        statement.setString(1, source)
                        statement.setString(2, target)
                }
                connection.close()
                println("Inserted emoji-skin-map entries successfully: $insertedEmoji")
        }

        private fun createStrokeTable(url: String) {
                val createTableCommand: String = "CREATE TABLE stroke_table(word TEXT NOT NULL, stroke TEXT NOT NULL, complex INTEGER NOT NULL, spell INTEGER NOT NULL, code INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created stroke table successfully.")
                val entries = Stroke.generate()
                val insertEntryCommand: String = "INSERT INTO stroke_table (word, stroke, complex, spell, code) VALUES (?, ?, ?, ?, ?);"
                println("Inserting ${entries.size} stroke entries...")
                val insertedStroke = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.stroke)
                        statement.setInt(3, entry.complex)
                        statement.setInt(4, entry.spell)
                        statement.setLong(5, entry.code)
                }
                connection.close()
                println("Inserted stroke entries successfully: $insertedStroke")
        }

        private fun createCangjieTable(url: String) {
                val createTableCommand: String = "CREATE TABLE cangjie_table(word TEXT NOT NULL, cangjie5 TEXT NOT NULL, c5complex INTEGER NOT NULL, c5code INTEGER NOT NULL, cangjie3 TEXT NOT NULL, c3complex INTEGER NOT NULL, c3code INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created cangjie table successfully.")
                val entries = Cangjie.generate()
                val insertEntryCommand: String = "INSERT INTO cangjie_table (word, cangjie5, c5complex, c5code, cangjie3, c3complex, c3code) VALUES (?, ?, ?, ?, ?, ?, ?);"
                println("Inserting ${entries.size} cangjie entries...")
                val insertedCangjie = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.cangjie5)
                        statement.setInt(3, entry.c5complex)
                        statement.setLong(4, entry.c5code)
                        statement.setString(5, entry.cangjie3)
                        statement.setInt(6, entry.c3complex)
                        statement.setLong(7, entry.c3code)
                }
                connection.close()
                println("Inserted cangjie entries successfully: $insertedCangjie")
        }

        private fun createQuickTable(url: String) {
                val createTableCommand: String = "CREATE TABLE quick_table(word TEXT NOT NULL, quick5 TEXT NOT NULL, q5complex INTEGER NOT NULL, q5code INTEGER NOT NULL, quick3 TEXT NOT NULL, q3complex INTEGER NOT NULL, q3code INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created quick table successfully.")
                val entries = Quick.generate()
                val insertEntryCommand: String = "INSERT INTO quick_table (word, quick5, q5complex, q5code, quick3, q3complex, q3code) VALUES (?, ?, ?, ?, ?, ?, ?);"
                println("Inserting ${entries.size} quick entries...")
                val insertedQuick = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.quick5)
                        statement.setInt(3, entry.q5complex)
                        statement.setLong(4, entry.q5code)
                        statement.setString(5, entry.quick3)
                        statement.setInt(6, entry.q3complex)
                        statement.setLong(7, entry.q3code)
                }
                connection.close()
                println("Inserted quick entries successfully: $insertedQuick")
        }
}
