package org.jyutping.jyutping.ninekey

data class SidebarEntry(
        val text: String,
        val isSelected: Boolean = false,
        val isSymbol: Boolean = false,
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is SidebarEntry) return false
                return text == other.text && isSelected == other.isSelected
        }
        override fun hashCode(): Int = text.hashCode() * 31 + isSelected.hashCode()

        companion object {
                val punctuation = listOf("，", "。", "？", "！", "、", "：", "；", "／", "…", "~", "～").map { SidebarEntry(text = it, isSymbol = true) }
                val symbols = listOf("+", "-", "*", "/", "=", "%", ":", "@", "#", ",", "$", "~", "≈").map { SidebarEntry(text = it, isSymbol = true) }
        }
}
