package org.jyutping.jyutping.keyboard

enum class ExtraBottomPadding(val identifier: Int) {
        None(0),
        Low(1),
        Medium(2),
        High(3);
        fun paddingValue(): Int = when(this) {
                None -> 0
                Low -> 24
                Medium -> 36
                High -> 48
        }
        companion object {
                fun paddingLevelOf(identifier: Int): ExtraBottomPadding = entries.find { it.identifier == identifier } ?: None
        }
}

/*
val fetchedBottomPadding: Dp = run {
        val navigationBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() // 24.dp
        val mandatorySystemGesturesBottom = WindowInsets.mandatorySystemGestures.asPaddingValues().calculateBottomPadding() // 48.dp
        val bottomPadding = max(navigationBarBottom, mandatorySystemGesturesBottom)
        if (bottomPadding > 0.dp) bottomPadding
        val systemGesturesBottom = WindowInsets.systemGestures.asPaddingValues().calculateBottomPadding() // 48.dp
        if (systemGesturesBottom > 0.dp) systemGesturesBottom else 48.dp
}
*/
