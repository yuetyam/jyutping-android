package org.jyutping.jyutping.emoji

import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.presets.PresetString

enum class EmojiCategory(val code: Int) {

        Frequent(0),
        SmileysAndPeople(1),
        AnimalsAndNature(2),
        FoodAndDrink(3),
        Activity(4),
        TravelAndPlaces(5),
        Objects(6),
        Symbols(7),
        Flags(8);

        companion object {
                fun categoryOf(value: Int): EmojiCategory? = entries.find { it.code == value }
        }
}

data class Emoji(
        val category: EmojiCategory,
        val unicodeVersion: Int,
        val identifier: Int,
        val text: String,
        val cantonese: String,
        val romanization: String
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Emoji) return false
                return this.category == other.category && this.text == other.text
        }

        override fun hashCode(): Int = category.hashCode() * 31 + text.hashCode()

        companion object {
                fun generateFrequentEmojis(emojiTexts: List<String>): List<Emoji> {
                        val cacheList: MutableList<Emoji> = mutableListOf()
                        for (index in emojiTexts.indices) {
                                val emojiText = emojiTexts[index]
                                val emojiUniqueNumber = index + 5000
                                val emoji = generateFrequentEmoji(emojiText, emojiUniqueNumber)
                                cacheList.add(emoji)
                        }
                        return cacheList
                }
                private fun generateFrequentEmoji(emojiText: String, emojiUniqueNumber: Int) = Emoji(
                        category = EmojiCategory.Frequent,
                        unicodeVersion = 110000,
                        identifier = emojiUniqueNumber,
                        text = emojiText,
                        cantonese = PresetString.EMPTY,
                        romanization = PresetString.EMPTY
                )

                fun categoryStartIndexMap(emojiSequence: List<Emoji>): Map<EmojiCategory, Int> {
                        var cacheIndex = PresetConstant.FrequentEmojiCount
                        val cacheMap: MutableMap<EmojiCategory, Int> = mutableMapOf()
                        for (category in EmojiCategory.entries) {
                                cacheMap[category] = cacheIndex
                                val newCount = emojiSequence.count { it.category == category }
                                cacheIndex += newCount
                        }
                        cacheMap[EmojiCategory.Frequent] = 0
                        return cacheMap
                }
        }
}
