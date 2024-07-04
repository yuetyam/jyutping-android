package org.jyutping.jyutping.extensions

fun String.charcode(): Int? {
        if (this.length >= 10) return null
        val codes = this.mapNotNull { it.intercode() }
        if (codes.size != this.length) return null
        return codes.combined()
}
fun String.shortcutCharcode(): Int? {
        if (this.length >= 10) return null
        val codes = this.mapNotNull { it.intercode() }
        if (codes.size != this.length) return null
        val code = codes
                .map { if (it == 44) 29 else it } // Replace 'y' with 'j'
                .combined()
        return code
}

fun Iterable<Int>.combined(): Int {
        if (this.count() >= 10) return 0
        return this.fold(0) { acc, i -> acc * 100 + i }
}

fun Char.intercode(): Int? = when (this) {
        'a' -> 20
        'b' -> 21
        'c' -> 22
        'd' -> 23
        'e' -> 24
        'f' -> 25
        'g' -> 26
        'h' -> 27
        'i' -> 28
        'j' -> 29
        'k' -> 30
        'l' -> 31
        'm' -> 32
        'n' -> 33
        'o' -> 34
        'p' -> 35
        'q' -> 36
        'r' -> 37
        's' -> 38
        't' -> 39
        'u' -> 40
        'v' -> 41
        'w' -> 42
        'x' -> 43
        'y' -> 44
        'z' -> 45
        else -> null
}
