package org.jyutping.preparing

fun readResourceLines(fileName: String): List<String> {
        val stream = object {}.javaClass.classLoader.getResourceAsStream(fileName) ?: error("Can not load $fileName")
        return stream.bufferedReader().use { it.readLines() }
}
