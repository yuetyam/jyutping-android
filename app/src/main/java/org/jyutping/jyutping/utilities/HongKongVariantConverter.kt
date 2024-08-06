package org.jyutping.jyutping.utilities

import org.jyutping.jyutping.extensions.empty

object HongKongVariantConverter {

        fun convert(text: String): String = text.map { variants[it] ?: it }.joinToString(separator = String.empty)

        private val variants: HashMap<Char, Char> = hashMapOf(
                '僞' to '偽',
                '兌' to '兑',
                '叄' to '叁',
                // '喫' to '吃',
                '囪' to '囱',
                '媼' to '媪',
                '嬀' to '媯',
                '悅' to '悦',
                '慍' to '愠',
                '戶' to '户',
                '挩' to '捝',
                '搵' to '揾',
                '擡' to '抬',
                '敓' to '敚',
                '敘' to '敍',
                '柺' to '枴',
                '梲' to '棁',
                '棱' to '稜',
                '榲' to '榅',
                '檯' to '枱',
                '氳' to '氲',
                '涗' to '涚',
                '溫' to '温',
                '溼' to '濕',
                '潙' to '溈',
                '潨' to '潀',
                '熅' to '煴',
                '爲' to '為',
                '癡' to '痴',
                '皁' to '皂',
                '祕' to '秘',
                '稅' to '税',
                '竈' to '灶',
                '糉' to '粽',
                '縕' to '緼',
                '纔' to '才',
                '脣' to '唇',
                '脫' to '脱',
                '膃' to '腽',
                '臥' to '卧',
                '臺' to '台',
                '菸' to '煙',
                '蒕' to '蒀',
                '蔥' to '葱',
                '蔿' to '蒍',
                '蘊' to '藴',
                '蛻' to '蜕',
                '衆' to '眾',
                '衛' to '衞',
                '覈' to '核',
                '說' to '説',
                '踊' to '踴',
                '轀' to '輼',
                '醞' to '醖',
                '鉢' to '缽',
                '鉤' to '鈎',
                '銳' to '鋭',
                '鍼' to '針',
                '閱' to '閲',
                '鰮' to '鰛'
        )
}