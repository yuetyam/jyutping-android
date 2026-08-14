package org.jyutping.preparing

object UnihanDefinition {
        fun generate(): List<Pair<Int, String>> {
                val characters = LexiconConverter.jyutpingSourceLines.mapNotNull { line ->
                        val word = line.substringBefore(PresetString.TAB).trim()
                        word.takeIf { it.characterCount() == 1 }
                }.distinct()
                return characters.mapNotNull { word ->
                        definitions[word]?.let { word.codePointAt(0) to it }
                }
        }

        private val definitions: Map<String, String> by lazy {
                buildMap {
                        readResourceLines("definition.txt").forEach { line ->
                                val parts = line.split(PresetString.TAB).map(String::trim).filter(String::isNotEmpty)
                                if (parts.size == 3) {
                                        putIfAbsent(parts[0], parts[2].replace("'", "’"))
                                }
                        }
                }
        }
}
