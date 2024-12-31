package org.jyutping.jyutping.keyboard

import android.view.inputmethod.EditorInfo

enum class ReturnKeyForm {

        BufferingSimplified,
        BufferingTraditional,
        StandbyABC,
        StandbySimplified,
        StandbyTraditional,
        UnavailableABC,
        UnavailableSimplified,
        UnavailableTraditional;

        fun keyText(imeAction: Int? = null): String? = when (this) {
                BufferingSimplified -> "确认"
                BufferingTraditional -> "確認"
                StandbyABC, UnavailableABC -> when (imeAction) {
                        EditorInfo.IME_ACTION_DONE -> "done"
                        EditorInfo.IME_ACTION_GO -> "go"
                        EditorInfo.IME_ACTION_NEXT -> "next"
                        EditorInfo.IME_ACTION_NONE -> null
                        EditorInfo.IME_ACTION_SEND -> "send"
                        EditorInfo.IME_ACTION_PREVIOUS -> "previous"
                        EditorInfo.IME_ACTION_SEARCH -> "search"
                        EditorInfo.IME_ACTION_UNSPECIFIED -> null
                        else -> null
                }
                StandbySimplified, UnavailableSimplified -> when (imeAction) {
                        EditorInfo.IME_ACTION_DONE -> "完成"
                        EditorInfo.IME_ACTION_GO -> "前往"
                        EditorInfo.IME_ACTION_NEXT -> "下一个"
                        EditorInfo.IME_ACTION_NONE -> null
                        EditorInfo.IME_ACTION_SEND -> "传送"
                        EditorInfo.IME_ACTION_PREVIOUS -> "上一个"
                        EditorInfo.IME_ACTION_SEARCH -> "搜寻"
                        EditorInfo.IME_ACTION_UNSPECIFIED -> null
                        else -> null
                }
                StandbyTraditional, UnavailableTraditional -> when (imeAction) {
                        EditorInfo.IME_ACTION_DONE -> "完成"
                        EditorInfo.IME_ACTION_GO -> "前往"
                        EditorInfo.IME_ACTION_NEXT -> "下一個"
                        EditorInfo.IME_ACTION_NONE -> null
                        EditorInfo.IME_ACTION_SEND -> "傳送"
                        EditorInfo.IME_ACTION_PREVIOUS -> "上一個"
                        EditorInfo.IME_ACTION_SEARCH -> "搜尋"
                        EditorInfo.IME_ACTION_UNSPECIFIED -> null
                        else -> null
                }
        }
}
