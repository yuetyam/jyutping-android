package org.jyutping.jyutping.keyboard

enum class SpaceKeyForm {

        English,
        Fallback,

        Lowercased,
        LowercasedSimplified,
        Uppercased,
        UppercasedSimplified,
        CapsLocked,
        CapsLockedSimplified,

        Confirm,
        ConfirmSimplified,
        Select,
        SelectSimplified;

        fun text(): String = when (this) {
                English -> "space"
                Fallback -> "空格"
                Lowercased -> "粵拼"
                LowercasedSimplified -> "粤拼·简"
                Uppercased -> "全寬空格"
                UppercasedSimplified -> "全宽空格"
                CapsLocked -> "大寫鎖定"
                CapsLockedSimplified -> "大写锁定"
                Confirm -> "確認"
                ConfirmSimplified -> "确认"
                Select -> "選定"
                SelectSimplified -> "选定"
        }
}
