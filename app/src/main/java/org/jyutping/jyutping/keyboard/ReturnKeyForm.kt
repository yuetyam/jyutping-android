package org.jyutping.jyutping.keyboard

enum class ReturnKeyForm {

        BufferingSimplified,
        BufferingTraditional,
        StandbyABC,
        StandbySimplified,
        StandbyTraditional,
        UnavailableABC,
        UnavailableSimplified,
        UnavailableTraditional;

        fun text(): String? = when (this) {
                BufferingSimplified -> "确认"
                BufferingTraditional -> "確認"
                StandbyABC -> null
                StandbySimplified -> null
                StandbyTraditional -> null
                UnavailableABC -> null
                UnavailableSimplified -> null
                UnavailableTraditional -> null
        }
}
