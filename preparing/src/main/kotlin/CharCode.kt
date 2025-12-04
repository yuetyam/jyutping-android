package org.jyutping.preparing

val String.charCode: Long?
        get() {
                if (this.length >= 10) return null
                val codes = this.mapNotNull { it.interCode }
                if (codes.size != this.length) return null
                return codes.radix100Combined()
        }

val String.nineKeyCharCode: Long?
        get() {
                if (this.length >= 19) return null
                val codes = this.mapNotNull { it.nineKenInterCode }
                if (codes.size != this.length) return null
                return codes.decimalCombined()
        }

fun Iterable<Int>.radix100Combined(): Long {
        if (this.count() >= 10) return 0
        return this.fold(0) { acc, i -> acc * 100 + i }
}
fun Iterable<Int>.decimalCombined(): Long {
        if (this.count() >= 19) return 0
        return this.fold(0) { acc, i -> acc * 10 + i }
}

val Char.interCode: Int?
        get() = CharCode.letterCodeMap[this]

val Char.nineKenInterCode: Int?
        get() = CharCode.nineKeyCodeMap[this]

private object CharCode {
        val letterCodeMap: HashMap<Char, Int> = hashMapOf(
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
                'z' to 45,
        )
        val nineKeyCodeMap: HashMap<Char, Int> = hashMapOf(
                'a' to 2,
                'b' to 2,
                'c' to 2,
                'd' to 3,
                'e' to 3,
                'f' to 3,
                'g' to 4,
                'h' to 4,
                'i' to 4,
                'j' to 5,
                'k' to 5,
                'l' to 5,
                'm' to 6,
                'n' to 6,
                'o' to 6,
                'p' to 7,
                'q' to 7,
                'r' to 7,
                's' to 7,
                't' to 8,
                'u' to 8,
                'v' to 8,
                'w' to 9,
                'x' to 9,
                'y' to 9,
                'z' to 9,
        )
}
