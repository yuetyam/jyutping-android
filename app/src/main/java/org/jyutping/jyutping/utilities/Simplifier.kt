package org.jyutping.jyutping.utilities

import org.jyutping.jyutping.extensions.empty

object Simplifier {
        fun convert(text: String, db: DatabaseHelper): String {
                return when (text.length) {
                        0 -> text
                        1 -> db.t2s(text.first())
                        2 -> phrases[text] ?: text.map { db.t2s(it) }.joinToString(separator = String.empty)
                        else -> phrases[text] ?: transform(text, db)
                }
        }
        private fun transform(text: String, db: DatabaseHelper): String {
                val roundOne = replace(text, "W")
                if (roundOne.matched.isEmpty()) return text.map { db.t2s(it) }.joinToString(separator = String.empty)
                val roundTwo = replace(roundOne.modified, "X")
                if (roundTwo.matched.isEmpty()) {
                        val transformed: String = roundTwo.modified.map { db.t2s(it) }.joinToString(separator = String.empty)
                        val reverted: String = transformed.replace(roundOne.replacement, roundOne.matched)
                        return reverted
                }
                val roundThree = replace(roundTwo.modified, "Y")
                if (roundThree.matched.isEmpty()) {
                        val transformed: String = roundThree.modified.map { db.t2s(it) }.joinToString(separator = String.empty)
                        val reverted: String = transformed
                                .replace(roundOne.replacement, roundOne.matched)
                                .replace(roundTwo.replacement, roundTwo.matched)
                        return reverted
                }
                val roundFour = replace(roundThree.modified, "Z")
                if (roundFour.matched.isEmpty()) {
                        val transformed: String = roundFour.modified.map { db.t2s(it) }.joinToString(separator = String.empty)
                        val reverted: String = transformed
                                .replace(roundOne.replacement, roundOne.matched)
                                .replace(roundTwo.replacement, roundTwo.matched)
                                .replace(roundThree.replacement, roundThree.matched)
                        return reverted
                }
                val transformed: String = roundFour.modified.map { db.t2s(it) }.joinToString(separator = String.empty)
                val reverted: String = transformed
                        .replace(roundOne.replacement, roundOne.matched)
                        .replace(roundTwo.replacement, roundTwo.matched)
                        .replace(roundThree.replacement, roundThree.matched)
                        .replace(roundFour.replacement, roundFour.matched)
                return reverted
        }
        private class Replaced(val modified: String, val matched: String, val replacement: String)
        private fun replace(text: String, replacement: String): Replaced {
                val textLength: Int = text.length
                val keys = phrases.keys.filter { it.length <= textLength }.sortedByDescending { it.length }
                var modified: String = text
                var matched: String = String.empty
                for (key in keys) {
                        if (text.startsWith(key)) {
                                modified = text.replace(key, replacement)
                                matched = phrases[key]!!
                                break
                        }
                }
                if (matched.isNotEmpty()) return Replaced(modified, matched, replacement)
                for (key in keys) {
                        if (text.contains(key)) {
                                modified = text.replace(key, replacement)
                                matched = phrases[key]!!
                                break
                        }
                }
                return Replaced(modified, matched, replacement)
        }

        private val phrases: HashMap<String, String> = hashMapOf(
                "一目瞭然" to "一目了然",
                "上鍊" to "上链",
                "不瞭解" to "不了解",
                "么麼" to "幺麽",
                "么麽" to "幺麽",
                "乾乾淨淨" to "干干净净",
                "乾乾脆脆" to "干干脆脆",
                "乾元" to "乾元",
                "乾卦" to "乾卦",
                "乾嘉" to "乾嘉",
                "乾圖" to "乾图",
                "乾坤" to "乾坤",
                "乾坤一擲" to "乾坤一掷",
                "乾坤再造" to "乾坤再造",
                "乾坤大挪移" to "乾坤大挪移",
                "乾宅" to "乾宅",
                "乾斷" to "乾断",
                "乾旦" to "乾旦",
                "乾曜" to "乾曜",
                "乾清宮" to "乾清宫",
                "乾盛世" to "乾盛世",
                "乾紅" to "乾红",
                "乾綱" to "乾纲",
                "乾縣" to "乾县",
                "乾象" to "乾象",
                "乾造" to "乾造",
                "乾道" to "乾道",
                "乾陵" to "乾陵",
                "乾隆" to "乾隆",
                "乾隆年間" to "乾隆年间",
                "乾隆皇帝" to "乾隆皇帝",
                "二噁英" to "二𫫇英",
                "以免藉口" to "以免借口",
                "以功覆過" to "以功复过",
                "侔德覆載" to "侔德复载",
                "傢俱" to "家具",
                "傷亡枕藉" to "伤亡枕藉",
                "八濛山" to "八濛山",
                "凌藉" to "凌借",
                "出醜狼藉" to "出丑狼藉",
                "函覆" to "函复",
                "千鍾粟" to "千锺粟",
                "反反覆覆" to "反反复复",
                "反覆" to "反复",
                "反覆思維" to "反复思维",
                "反覆思量" to "反复思量",
                "反覆性" to "反复性",
                "名覆金甌" to "名复金瓯",
                "哪吒" to "哪吒",
                "回覆" to "回复",
                "壺裏乾坤" to "壶里乾坤",
                // "大目乾連冥間救母變文" to "大目乾连冥间救母变文",
                "宫商角徵羽" to "宫商角徵羽",
                "射覆" to "射复",
                "尼乾陀" to "尼乾陀",
                "幺麼" to "幺麽",
                "幺麼小丑" to "幺麽小丑",
                "幺麼小醜" to "幺麽小丑",
                "康乾" to "康乾",
                "張法乾" to "张法乾",
                "彷彿" to "仿佛",
                "彷徨" to "彷徨",
                "徵弦" to "徵弦",
                "徵絃" to "徵弦",
                "徵羽摩柯" to "徵羽摩柯",
                "徵聲" to "徵声",
                "徵調" to "徵调",
                "徵音" to "徵音",
                "情有獨鍾" to "情有独钟",
                "憑藉" to "凭借",
                "憑藉着" to "凭借着",
                "手鍊" to "手链",
                "扭轉乾坤" to "扭转乾坤",
                "找藉口" to "找借口",
                "拉鍊" to "拉链",
                "拉鍊工程" to "拉链工程",
                "拜覆" to "拜复",
                "據瞭解" to "据了解",
                "文錦覆阱" to "文锦复阱",
                "於世成" to "於世成",
                "於乎" to "於乎",
                "於仲完" to "於仲完",
                "於倫" to "於伦",
                "於其一" to "於其一",
                "於則" to "於则",
                "於勇明" to "於勇明",
                "於呼哀哉" to "於呼哀哉",
                "於單" to "於单",
                "於坦" to "於坦",
                "於崇文" to "於崇文",
                "於忠祥" to "於忠祥",
                "於惟一" to "於惟一",
                "於戲" to "於戏",
                "於敖" to "於敖",
                "於梨華" to "於梨华",
                "於清言" to "於清言",
                "於潛" to "於潜",
                "於琳" to "於琳",
                "於穆" to "於穆",
                "於竹屋" to "於竹屋",
                "於菟" to "於菟",
                "於邑" to "於邑",
                "於陵子" to "於陵子",
                "旋乾轉坤" to "旋乾转坤",
                "旋轉乾坤" to "旋转乾坤",
                "旋轉乾坤之力" to "旋转乾坤之力",
                "明瞭" to "明了",
                "明覆" to "明复",
                "書中自有千鍾粟" to "书中自有千锺粟",
                "有序" to "有序",
                "朝乾夕惕" to "朝乾夕惕",
                "木吒" to "木吒",
                "李乾德" to "李乾德",
                "李澤鉅" to "李泽钜",
                "李鍊福" to "李链福",
                "李鍾郁" to "李锺郁",
                "樊於期" to "樊於期",
                "沈沒" to "沉没",
                "沈沒成本" to "沉没成本",
                "沈積" to "沉积",
                "沈船" to "沉船",
                "沈默" to "沉默",
                "流徵" to "流徵",
                "浪蕩乾坤" to "浪荡乾坤",
                "滑藉" to "滑借",
                "無序" to "无序",
                "牴牾" to "抵牾",
                "牴觸" to "抵触",
                "狐藉虎威" to "狐借虎威",
                "珍珠項鍊" to "珍珠项链",
                "甚鉅" to "甚钜",
                "申覆" to "申复",
                "畢昇" to "毕昇",
                "發覆" to "发复",
                "瞭如" to "了如",
                "瞭如指掌" to "了如指掌",
                "瞭望" to "瞭望",
                "瞭然" to "了然",
                "瞭然於心" to "了然于心",
                "瞭若指掌" to "了若指掌",
                "瞭解" to "了解",
                "瞭解到" to "了解到",
                "示覆" to "示复",
                "神祇" to "神祇",
                "稟覆" to "禀复",
                "竺乾" to "竺乾",
                "答覆" to "答复",
                "篤麼" to "笃麽",
                "簡單明瞭" to "简单明了",
                "籌畫" to "筹划",
                "素藉" to "素借",
                "老態龍鍾" to "老态龙钟",
                "肘手鍊足" to "肘手链足",
                "茵藉" to "茵借",
                "萬鍾" to "万锺",
                "蒜薹" to "蒜薹",
                "蕓薹" to "芸薹",
                "蕩覆" to "荡复",
                "蕭乾" to "萧乾",
                "藉代" to "借代",
                "藉以" to "借以",
                "藉助" to "借助",
                "藉助於" to "借助于",
                "藉卉" to "借卉",
                "藉口" to "借口",
                "藉喻" to "借喻",
                "藉寇兵" to "借寇兵",
                "藉寇兵齎盜糧" to "借寇兵赍盗粮",
                "藉手" to "借手",
                "藉據" to "借据",
                "藉故" to "借故",
                "藉故推辭" to "借故推辞",
                "藉方" to "借方",
                "藉條" to "借条",
                "藉槁" to "借槁",
                "藉機" to "借机",
                "藉此" to "借此",
                "藉此機會" to "借此机会",
                "藉甚" to "借甚",
                "藉由" to "借由",
                "藉着" to "借着",
                "藉端" to "借端",
                "藉端生事" to "借端生事",
                "藉箸代籌" to "借箸代筹",
                "藉草枕塊" to "借草枕块",
                "藉藉" to "藉藉",
                "藉藉无名" to "藉藉无名",
                "藉詞" to "借词",
                "藉讀" to "借读",
                "藉資" to "借资",
                "衹得" to "只得",
                "衹見樹木" to "只见树木",
                // "衹見樹木不見森林" to "只见树木不见森林",
                "袖裏乾坤" to "袖里乾坤",
                "覆上" to "复上",
                "覆住" to "复住",
                "覆信" to "复信",
                "覆冒" to "复冒",
                "覆呈" to "复呈",
                "覆命" to "复命",
                "覆墓" to "复墓",
                "覆宗" to "复宗",
                "覆帳" to "复帐",
                "覆幬" to "复帱",
                "覆成" to "复成",
                "覆按" to "复按",
                "覆文" to "复文",
                "覆杯" to "复杯",
                "覆校" to "复校",
                "覆瓿" to "复瓿",
                "覆盂" to "复盂",
                "覆盆" to "覆盆",
                "覆盆子" to "覆盆子",
                "覆盤" to "覆盘",
                "覆育" to "复育",
                "覆蕉尋鹿" to "复蕉寻鹿",
                "覆逆" to "复逆",
                "覆醢" to "复醢",
                "覆醬瓿" to "复酱瓿",
                "覆電" to "复电",
                "覆露" to "复露",
                "覆鹿尋蕉" to "复鹿寻蕉",
                "覆鹿遺蕉" to "复鹿遗蕉",
                "覆鼎" to "复鼎",
                "見覆" to "见复",
                "角徵" to "角徵",
                "角徵羽" to "角徵羽",
                "計畫" to "计划",
                "變徵" to "变徵",
                "變徵之聲" to "变徵之声",
                "變徵之音" to "变徵之音",
                "貂覆額" to "貂复额",
                "買臣覆水" to "买臣复水",
                "踅門瞭戶" to "踅门了户",
                "躪藉" to "躏借",
                "郭子乾" to "郭子乾",
                "酒逢知己千鍾少" to "酒逢知己千锺少",
                // "酒逢知己千鍾少話不投機半句多" to "酒逢知己千锺少话不投机半句多",
                "醞藉" to "酝借",
                "重覆" to "重复",
                "金吒" to "金吒",
                "金鍊" to "金链",
                "鈞覆" to "钧复",
                "鉅子" to "钜子",
                "鉅萬" to "钜万",
                "鉅防" to "钜防",
                "鉸鍊" to "铰链",
                "銀鍊" to "银链",
                "錢鍾書" to "钱锺书",
                "鍊墜" to "链坠",
                "鍊子" to "链子",
                "鍊形" to "链形",
                "鍊條" to "链条",
                "鍊錘" to "链锤",
                "鍊鎖" to "链锁",
                "鍛鍾" to "锻锺",
                "鍾繇" to "钟繇",
                "鍾萬梅" to "锺万梅",
                "鍾重發" to "锺重发",
                "鍾鍛" to "锺锻",
                "鍾馗" to "锺馗",
                "鎖鍊" to "锁链",
                "鐵鍊" to "铁链",
                "鑽石項鍊" to "钻石项链",
                "雁杳魚沈" to "雁杳鱼沉",
                "雖覆能復" to "虽覆能复",
                "電覆" to "电复",
                "露覆" to "露复",
                "項鍊" to "项链",
                "頗覆" to "颇复",
                "頸鍊" to "颈链",
                "顛乾倒坤" to "颠乾倒坤",
                "顛倒乾坤" to "颠倒乾坤",
                "顧藉" to "顾借",
                "麼些族" to "麽些族",
                "黄鍾公" to "黄锺公",
                "龍鍾" to "龙钟",
                "甚麼" to "什么",
        )
}
