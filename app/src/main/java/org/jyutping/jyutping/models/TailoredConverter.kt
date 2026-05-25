package org.jyutping.jyutping.models

import android.database.sqlite.SQLiteStatement
import org.jyutping.jyutping.extensions.characterCount
import org.jyutping.jyutping.extensions.isIdeographicCodePoint
import org.jyutping.jyutping.extensions.negative

object TailoredConverter {
        private fun char2char(code: Int, statement: SQLiteStatement): Int {
                statement.clearBindings()
                statement.bindLong(1, code.toLong())
                val target = statement.simpleQueryForLong()
                return if (target < 1) code else target.toInt()
        }
        fun transformed(lexicons: List<Lexicon>, commentForm: RomanizationForm, sessionState: Long, statement: SQLiteStatement): List<Candidate> {
                return lexicons.map { lexicon ->
                        if (lexicon.isNotCantonese) return@map Candidate(lexicon = lexicon, commentForm = commentForm, sessionState = sessionState)
                        val convertedText = perform(lexicon.text, statement)
                        return@map Candidate(text = convertedText, lexicon = lexicon, commentForm = commentForm, sessionState = sessionState)
                }.distinct()
        }
        private fun perform(text: String, statement: SQLiteStatement): String {
                return when (text.characterCount) {
                        0 -> text
                        1 -> char2char(text.codePointAt(0), statement).let { buildString { appendCodePoint(it) } }
                        2 -> phrases[text] ?: run {
                                val codes = text.codePoints().map { char2char(it, statement) }
                                buildString { codes.forEachOrdered { appendCodePoint(it) } }
                        }
                        else -> phrases[text] ?: transform(text, statement)
                }
        }
        private fun transform(text: String, statement: SQLiteStatement): String {
                val codePoints = text.codePoints().toArray()
                val charCount = codePoints.size
                val result = StringBuilder(charCount)
                var index = 0
                while (index < charCount) {
                        var matched = false
                        val maxLength = minOf(maxPhraseLength, charCount - index)
                        for (length in maxLength downTo MIN_LENGTH) {
                                val substring = String(codePoints, index, length)
                                val replacement = phrases[substring]
                                if (replacement != null) {
                                        result.append(replacement)
                                        index += length
                                        matched = true
                                        break
                                }
                        }
                        if (matched.negative) {
                                val codePoint = codePoints[index]
                                val convertedCodePoint: Int = if (codePoint.isIdeographicCodePoint) char2char(codePoint, statement) else codePoint
                                result.append(Character.toString(convertedCodePoint))
                                index += 1
                        }
                }
                return result.toString()
        }

        private val phrases: Map<String, String> = mapOf(
                "上鍊" to "上鏈",
                "么麼" to "幺麽",
                "么麽" to "幺麽",
                "以功覆過" to "以功覆過",
                "侔德覆載" to "侔德覆載",
                "傢俱" to "傢具",
                "函覆" to "函復",
                "反反覆覆" to "反反復復",
                "反覆" to "反復",
                "反覆思維" to "反復思維",
                "反覆思量" to "反復思量",
                "反覆性" to "反復性",
                "名覆金甌" to "名復金甌",
                "哪吒" to "哪吒",
                "回覆" to "回復",
                "射覆" to "射覆",
                "彷彿" to "仿佛",
                "彷徨" to "彷徨",
                "手鍊" to "手鏈",
                "拉鍊" to "拉鏈",
                "拉鍊工程" to "拉鏈工程",
                "拜覆" to "拜復",
                "文錦覆阱" to "文錦覆阱",
                "於世成" to "於世成",
                "於乎" to "於乎",
                "於仲完" to "於仲完",
                "於倫" to "於倫",
                "於其一" to "於其一",
                "於則" to "於則",
                "於勇明" to "於勇明",
                "於呼哀哉" to "於呼哀哉",
                "於單" to "於單",
                "於坦" to "於坦",
                "於崇文" to "於崇文",
                "於忠祥" to "於忠祥",
                "於惟一" to "於惟一",
                "於戲" to "於戲",
                "於敖" to "於敖",
                "於梨華" to "於梨華",
                "於清言" to "於清言",
                "於潛" to "於潜",
                "於琳" to "於琳",
                "於穆" to "於穆",
                "於竹屋" to "於竹屋",
                "於菟" to "於菟",
                "於邑" to "於邑",
                "於陵子" to "於陵子",
                "明覆" to "明復",
                "木吒" to "木吒",
                "李澤鉅" to "李澤鉅",
                "李鍊福" to "李鏈福",
                "束脩" to "束脩",
                "樊於期" to "樊於期",
                "沈沒" to "沉没",
                "沈沒成本" to "沉没成本",
                "沈積" to "沉積",
                "沈船" to "沉船",
                "沈默" to "沉默",
                "珍珠項鍊" to "珍珠項鏈",
                "甚鉅" to "甚鉅",
                "申覆" to "申復",
                "畢昇" to "畢昇",
                "發覆" to "發覆",
                "示覆" to "示復",
                "稟覆" to "禀復",
                "答覆" to "答復",
                "肘手鍊足" to "肘手鏈足",
                "脩敬" to "脩敬",
                "脩脯" to "脩脯",
                "脩金" to "脩金",
                "蕩覆" to "蕩覆",
                "覆上" to "覆上",
                "覆住" to "覆住",
                "覆信" to "復信",
                "覆冒" to "覆冒",
                "覆呈" to "復呈",
                "覆命" to "復命",
                "覆墓" to "復墓",
                "覆宗" to "覆宗",
                "覆帳" to "復帳",
                "覆幬" to "覆幬",
                "覆成" to "覆成",
                "覆按" to "復按",
                "覆文" to "復文",
                "覆杯" to "覆杯",
                "覆校" to "復校",
                "覆瓿" to "覆瓿",
                "覆盂" to "覆盂",
                "覆盆" to "覆盆",
                "覆盆子" to "覆盆子",
                "覆盤" to "覆盤",
                "覆育" to "覆育",
                "覆蕉尋鹿" to "覆蕉尋鹿",
                "覆逆" to "覆逆",
                "覆醢" to "覆醢",
                "覆醬瓿" to "覆醬瓿",
                "覆電" to "復電",
                "覆露" to "覆露",
                "覆鹿尋蕉" to "覆鹿尋蕉",
                "覆鹿遺蕉" to "覆鹿遺蕉",
                "覆鼎" to "覆鼎",
                "見覆" to "見復",
                "貂覆額" to "貂覆額",
                "買臣覆水" to "買臣覆水",
                "重覆" to "重復",
                "金吒" to "金吒",
                "金鍊" to "金鏈",
                "鈞覆" to "鈞復",
                "鉅子" to "鉅子",
                "鉅萬" to "鉅萬",
                "鉅防" to "鉅防",
                "鉸鍊" to "鉸鏈",
                "銀鍊" to "銀鏈",
                "鍊墜" to "鏈墜",
                "鍊子" to "鏈子",
                "鍊形" to "鏈形",
                "鍊條" to "鏈條",
                "鍊錘" to "鏈錘",
                "鍊鎖" to "鏈鎖",
                "鎖鍊" to "鎖鏈",
                "鐵鍊" to "鐵鏈",
                "鑽石項鍊" to "鑽石項鏈",
                "雁杳魚沈" to "雁杳魚沉",
                "雖覆能復" to "雖覆能復",
                "電覆" to "電復",
                "露覆" to "露覆",
                "項鍊" to "項鏈",
                "頗覆" to "頗覆",
                "頸鍊" to "頸鏈",
        )

        private const val MIN_LENGTH: Int = 2
        private val maxPhraseLength: Int = phrases.keys.maxOf { it.characterCount }
}
