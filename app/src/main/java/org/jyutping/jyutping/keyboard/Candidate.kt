package org.jyutping.jyutping.keyboard

data class Candidate(
        val type: CandidateType = CandidateType.CANTONESE,
        val text: String,
        val lexiconText: String = text,
        val romanization: String,
        val input: String,
        val mark: String = input,
        val order: Int = 0
)
