package org.jyutping.jyutping.search

data class Pronunciation(
        val romanization: String,
        val homophones: List<String> = listOf(),
        val interpretation: String? = null,
        val collocations: List<String> = listOf()
)

data class CantoneseLexicon(
        val text: String,
        val pronunciations: List<Pronunciation> = listOf(),
        val note: String? = null
)
