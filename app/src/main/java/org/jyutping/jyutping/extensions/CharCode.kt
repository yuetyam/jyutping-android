package org.jyutping.jyutping.extensions

fun String.charcode(): Long? {
        if (this.length >= 10) return null
        val codes = this.mapNotNull { it.intercode() }
        if (codes.size != this.length) return null
        return codes.radix100Combined()
}
fun String.anchorsCode(): Long? {
        if (this.length >= 10) return null
        val codes = this.mapNotNull { it.intercode() }
        if (codes.size != this.length) return null
        val code = codes
                .map { if (it == 44) 29 else it } // Replace 'y' with 'j'
                .radix100Combined()
        return code
}
fun Iterable<Char>.anchorsCode(): Long? {
        if (this.count() >= 10) return null
        val codes = this.mapNotNull { it.intercode() }
        if (codes.size != this.count()) return null
        val code = codes
                .map { if (it == 44) 29 else it } // Replace 'y' with 'j'
                .radix100Combined()
        return code
}
fun Iterable<Int>.radix100Combined(): Long {
        if (this.count() >= 10) return 0
        return this.fold(0) { acc, i -> acc * 100 + i }
}

fun Char.intercode(): Int? = CharCode.letterCodeMap[this]

val Char.virtualInputCode: Int?
        get() = CharCode.letterCodeMap[this] ?: CharCode.numberCodeMap[this]

private object CharCode {
        val letterCodeMap: Map<Char, Int> = mapOf(
                'a' to 20,
                'b' to 21,
                'c' to 22,
                'd' to 23,
                'e' to 24,
                'f' to 25,
                'g' to 26,
                'h' to 27,
                'i' to 28,
                'j' to 29,
                'k' to 30,
                'l' to 31,
                'm' to 32,
                'n' to 33,
                'o' to 34,
                'p' to 35,
                'q' to 36,
                'r' to 37,
                's' to 38,
                't' to 39,
                'u' to 40,
                'v' to 41,
                'w' to 42,
                'x' to 43,
                'y' to 44,
                'z' to 45
        )
        val numberCodeMap: Map<Char, Int> = mapOf(
                '0' to 10,
                '1' to 11,
                '2' to 12,
                '3' to 13,
                '4' to 14,
                '5' to 15,
                '6' to 16,
                '7' to 17,
                '8' to 18,
                '9' to 19
        )
}
