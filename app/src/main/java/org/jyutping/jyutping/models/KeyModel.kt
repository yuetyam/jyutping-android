package org.jyutping.jyutping.models

data class KeyElement(
        val text: String,
        val header: String? = null,
        val footer: String? = null
) {
        val isTextSingular: Boolean
                get() = text.length == 1
}

data class KeyModel(
        val primary: KeyElement,
        val members: List<KeyElement>
) {
        val isExpansible: Boolean
                get() = members.size > 1
}
