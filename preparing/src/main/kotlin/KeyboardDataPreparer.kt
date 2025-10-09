package org.jyutping.preparing

import java.io.InputStream
import java.sql.DriverManager
import kotlin.use

object KeyboardDataPreparer {
        fun prepare(url: String) {
                createLexiconTable(url)
                createHant2HansTable(url)
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
                createIndies(url)
        }
        private fun createIndies(url: String) {
                val commands: List<String> = listOf(
                        "CREATE INDEX lexiconpingindex ON lexicontable(ping);",
                        "CREATE INDEX lexiconanchorsindex ON lexicontable(anchors);",
                        "CREATE INDEX lexiconstrictindex ON lexicontable(ping, anchors);",
                        "CREATE INDEX lexicontenkeycodeindex ON lexicontable(tenkeycode);",
                        "CREATE INDEX lexicontenkeyanchorsindex ON lexicontable(tenkeyanchors);",
                        "CREATE INDEX lexiconwordindex ON lexicontable(word);",
                        "CREATE INDEX lexiconromanizationindex ON lexicontable(romanization);",

                        "CREATE INDEX structurepingindex ON structuretable(ping);",
                        "CREATE INDEX structuretenkeycodeindex ON structuretable(tenkeycode);",

                        "CREATE INDEX pinyinpingindex ON pinyintable(ping);",
                        "CREATE INDEX pinyinanchorsindex ON pinyintable(anchors);",
                        "CREATE INDEX pinyinstrictindex ON pinyintable(ping, anchors);",
                        "CREATE INDEX pinyintenkeycodeindex ON pinyintable(tenkeycode);",
                        "CREATE INDEX pinyintenkeyanchorsindex ON pinyintable(tenkeyanchors);",

                        "CREATE INDEX symbolpingindex ON symboltable(ping);",
                        "CREATE INDEX symboltenkeycodeindex ON symboltable(tenkeycode);",
                        "CREATE INDEX emojiskinmapindex ON emojiskinmap(source);",

                        "CREATE INDEX markpingindex ON marktable(ping);",
                        "CREATE INDEX markcodeindex ON marktable(code);",
                        "CREATE INDEX marktenkeycodeindex ON marktable(tenkeycode);",

                        "CREATE INDEX syllabletenkeyindex ON syllabletable(tenkeyaliascode);",
                        "CREATE INDEX pinyinsyllabletenkeyindex ON pinyinsyllabletable(tenkeycode);",

                        "CREATE INDEX strokestrokeindex ON stroketable(stroke);",
                        "CREATE INDEX strokepingindex ON stroketable(ping);",
                        "CREATE INDEX strokecodeindex ON stroketable(code);",

                        "CREATE INDEX cangjiecangjie5index ON cangjietable(cangjie5);",
                        "CREATE INDEX cangjiec5codeindex ON cangjietable(c5code);",
                        "CREATE INDEX cangjiecangjie3index ON cangjietable(cangjie3);",
                        "CREATE INDEX cangjiec3codeindex ON cangjietable(c3code);",

                        "CREATE INDEX quickquick5index ON quicktable(quick5);",
                        "CREATE INDEX quickq5codeindex ON quicktable(q5code);",
                        "CREATE INDEX quickquick3index ON quicktable(quick3);",
                        "CREATE INDEX quickq3codeindex ON quicktable(q3code);",
                )
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        for (command in commands) {
                                statement.executeUpdate(command)
                        }
                }
                connection.close()
                println("Successfully created keyboard data indies.")
        }

        private fun createLexiconTable(url: String) {
                val createTableCommand: String = "CREATE TABLE lexicontable(word TEXT NOT NULL, romanization TEXT NOT NULL, anchors INTEGER NOT NULL, ping INTEGER NOT NULL, tenkeyanchors INTEGER NOT NULL, tenkeycode INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created lexicon table successfully.")
                val entries = LexiconConverter.jyutping()
                val insertEntryCommand: String = "INSERT INTO lexicontable (word, romanization, anchors, ping, tenkeyanchors, tenkeycode) VALUES (?, ?, ?, ?, ?, ?);"
                println("Inserting ${entries.size} lexicon entries...")
                val inserted = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.romanization)
                        statement.setLong(3, entry.anchors)
                        statement.setLong(4, entry.ping)
                        statement.setLong(5, entry.tenKeyAnchors)
                        statement.setLong(6, entry.tenKeyCode)
                }
                connection.close()
                println("Inserted lexicon entries successfully: $inserted")
        }

        private fun createHant2HansTable(url: String) {
                val createTableCommand: String = "CREATE TABLE t2stable(traditional INTEGER NOT NULL PRIMARY KEY, simplified INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created t2s table successfully.")
                val entries = Hant2Hans.generate()
                val insertEntryCommand: String = "INSERT INTO t2stable (traditional, simplified) VALUES (?, ?);"
                println("Inserting ${entries.size} t2s entries...")
                val insertedT2s = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setInt(1, entry.first)
                        statement.setInt(2, entry.second)
                }
                connection.close()
                println("Inserted t2s entries successfully: $insertedT2s")
        }

        private fun createStructureTable(url: String) {
                val createTableCommand: String = "CREATE TABLE structuretable(word TEXT NOT NULL, romanization TEXT NOT NULL, ping INTEGER NOT NULL, tenkeycode INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created structure table successfully.")
                val entries = LexiconConverter.structure()
                val insertEntryCommand: String = "INSERT INTO structuretable (word, romanization, ping, tenkeycode) VALUES (?, ?, ?, ?);"
                println("Inserting ${entries.size} structure entries...")
                val inserted = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.romanization)
                        statement.setLong(3, entry.ping)
                        statement.setLong(4, entry.tenKeyCode)
                }
                connection.close()
                println("Inserted structure entries successfully: $inserted")
        }

        private fun createPinyinTable(url: String) {
                val createTableCommand: String = "CREATE TABLE pinyintable(word TEXT NOT NULL, romanization TEXT NOT NULL, anchors INTEGER NOT NULL, ping INTEGER NOT NULL, tenkeyanchors INTEGER NOT NULL, tenkeycode INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created pinyin table successfully.")
                val entries = LexiconConverter.pinyin()
                val insertEntryCommand: String = "INSERT INTO pinyintable (word, romanization, anchors, ping, tenkeyanchors, tenkeycode) VALUES (?, ?, ?, ?, ?, ?);"
                println("Inserting ${entries.size} pinyin entries...")
                val insertedPinyin = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.romanization)
                        statement.setLong(3, entry.anchors)
                        statement.setLong(4, entry.ping)
                        statement.setLong(5, entry.tenKeyAnchors)
                        statement.setLong(6, entry.tenKeyCode)
                }
                connection.close()
                println("Inserted pinyin entries successfully: $insertedPinyin")
        }

        private fun createSyllablesTable(url: String) {
                val createTableCommand: String = "CREATE TABLE syllabletable(aliascode INTEGER NOT NULL PRIMARY KEY, origincode INTEGER NOT NULL, tenkeyaliascode INTEGER NOT NULL, tenkeyorigincode INTEGER NOT NULL, alias TEXT NOT NULL, origin TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created syllable table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("syllable.txt") ?: error("Can not load syllable.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO syllabletable (aliascode, origincode, tenkeyaliascode, tenkeyorigincode, alias, origin) VALUES (?, ?, ?, ?, ?, ?);"
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
                        val tenKeyAliasCode = alias.tenKeyCharCode
                        val tenKeyOriginCode = origin.tenKeyCharCode
                        if (tenKeyAliasCode == null || tenKeyOriginCode == null) error(badLineFormat)
                        if (tenKeyAliasCode == 0L || tenKeyOriginCode == 0L) error(badLineFormat)
                        statement.setLong(1, aliasCode)
                        statement.setLong(2, originCode)
                        statement.setLong(3, tenKeyAliasCode)
                        statement.setLong(4, tenKeyOriginCode)
                        statement.setString(5, alias)
                        statement.setString(6, origin)
                }
                connection.close()
                println("Inserted syllable entries successfully: $insertedSyllable")
        }

        private fun createPinyinSyllablesTable(url: String) {
                val createTableCommand: String = "CREATE TABLE pinyinsyllabletable(code INTEGER NOT NULL PRIMARY KEY, tenkeycode INTEGER NOT NULL, syllable TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created pinyin syllable table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("pinyin-syllable.txt") ?: error("Can not load pinyin-syllable.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO pinyinsyllabletable (code, tenkeycode, syllable) VALUES (?, ?, ?);"
                println("Inserting ${sourceLines.size} pinyin-syllable entries...")
                val insertedPinyinSyll = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val code = line.charCode
                        val tenKeyCode = line.tenKeyCharCode
                        if (code == null || tenKeyCode == null) error(badLineFormat)
                        if (code == 0L || tenKeyCode == 0L) error(badLineFormat)
                        statement.setLong(1, code)
                        statement.setLong(2, tenKeyCode)
                        statement.setString(3, line)
                }
                connection.close()
                println("Inserted pinyin syllable entries successfully: $insertedPinyinSyll")
        }

        private fun createTextMarkTable(url: String) {
                val createTableCommand: String = "CREATE TABLE marktable(input TEXT NOT NULL, mark TEXT NOT NULL, ping INTEGER NOT NULL, code INTEGER NOT NULL, tenkeycode INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created text mark table successfully.")
                val insertEntryCommand: String = "INSERT INTO marktable (input, mark, ping, code, tenkeycode) VALUES (?, ?, ?, ?, ?);"
                val items = TextMarkLexicon.generate()
                println("Inserting ${items.size} text-mark entries...")
                val insertedMarks = batchInsert(connection, insertEntryCommand, items) { statement, item ->
                        statement.setString(1, item.input)
                        statement.setString(2, item.mark)
                        statement.setInt(3, item.pingCode)
                        statement.setLong(4, item.charCode)
                        statement.setLong(5, item.tenKeyCode)
                }
                connection.close()
                println("Inserted text mark entries successfully: $insertedMarks")
        }

        private fun createSymbolTable(url: String) {
                val createTableCommand: String = "CREATE TABLE symboltable(category INTEGER NOT NULL, unicodeversion INTEGER NOT NULL, codepoint TEXT NOT NULL, cantonese TEXT NOT NULL, romanization TEXT NOT NULL, ping INTEGER NOT NULL, tenkeycode INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created symbol table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("symbol.txt") ?: error("Can not load symbol.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO symboltable (category, unicodeversion, codepoint, cantonese, romanization, ping, tenkeycode) VALUES (?, ?, ?, ?, ?, ?, ?);"
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
                        val pingCode = syllableText.hashCode()
                        val tenKeyCode = syllableText.tenKeyCharCode ?: 0
                        statement.setInt(1, category)
                        statement.setInt(2, version)
                        statement.setString(3, codePoint)
                        statement.setString(4, cantonese)
                        statement.setString(5, romanization)
                        statement.setInt(6, pingCode)
                        statement.setLong(7, tenKeyCode)
                }
                connection.close()
                println("Inserted symbol entries successfully: $insertedSymbols")
        }
        private fun createEmojiSkinMapTable(url: String) {
                val createTableCommand: String = "CREATE TABLE emojiskinmap(source TEXT NOT NULL, target TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created emoji-skin-map table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("skin-tone-map.txt") ?: error("Can not load skin-tone-map.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO emojiskinmap (source, target) VALUES (?, ?);"
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
                val createTableCommand: String = "CREATE TABLE stroketable(word TEXT NOT NULL, stroke TEXT NOT NULL, complex INTEGER NOT NULL, ping INTEGER NOT NULL, code INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created stroke table successfully.")
                val entries = Stroke.generate()
                val insertEntryCommand: String = "INSERT INTO stroketable (word, stroke, complex, ping, code) VALUES (?, ?, ?, ?, ?);"
                println("Inserting ${entries.size} stroke entries...")
                val insertedStroke = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setString(1, entry.word)
                        statement.setString(2, entry.stroke)
                        statement.setInt(3, entry.complex)
                        statement.setInt(4, entry.ping)
                        statement.setLong(5, entry.code)
                }
                connection.close()
                println("Inserted stroke entries successfully: $insertedStroke")
        }

        private fun createCangjieTable(url: String) {
                val createTableCommand: String = "CREATE TABLE cangjietable(word TEXT NOT NULL, cangjie5 TEXT NOT NULL, c5complex INTEGER NOT NULL, c5code INTEGER NOT NULL, cangjie3 TEXT NOT NULL, c3complex INTEGER NOT NULL, c3code INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created cangjie table successfully.")
                val entries = Cangjie.generate()
                val insertEntryCommand: String = "INSERT INTO cangjietable (word, cangjie5, c5complex, c5code, cangjie3, c3complex, c3code) VALUES (?, ?, ?, ?, ?, ?, ?);"
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
                val createTableCommand: String = "CREATE TABLE quicktable(word TEXT NOT NULL, quick5 TEXT NOT NULL, q5complex INTEGER NOT NULL, q5code INTEGER NOT NULL, quick3 TEXT NOT NULL, q3complex INTEGER NOT NULL, q3code INTEGER NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created quick table successfully.")
                val entries = Quick.generate()
                val insertEntryCommand: String = "INSERT INTO quicktable (word, quick5, q5complex, q5code, quick3, q3complex, q3code) VALUES (?, ?, ?, ?, ?, ?, ?);"
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
