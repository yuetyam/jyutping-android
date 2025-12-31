package org.jyutping.jyutping.models

import org.jyutping.jyutping.utilities.DatabaseHelper

object Structure {
        fun reverseLookup(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Lexicon> {
                return search(text = text, segmentation = segmentation, db = db)
                        .flatMap { item ->
                                db.reverseLookup(text = item.text)
                                        .map { romanization -> Lexicon(text = item.text, romanization = romanization, input = text) }
                        }
        }
        private fun search(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Lexicon> {
                val matched = db.structureMatch(text = text)
                val textLength = text.length
                val schemes = segmentation.filter { it.length == textLength }
                val queried = schemes.flatMap { db.structureMatch(text = it.originText) }
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
