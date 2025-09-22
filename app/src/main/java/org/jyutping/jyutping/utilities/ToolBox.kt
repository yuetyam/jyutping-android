package org.jyutping.jyutping.utilities

import androidx.compose.ui.graphics.Color
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

object ToolBox {

        /** Background color for this action key.
         * @param isDarkMode In night mode color scheme?
         * @param isHighContrastPreferred Prefer high contrast color scheme?
         * @param isPressing Is interacting?
         * @return Background color for this action key.
         */
        fun actionKeyBackColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean, isPressing: Boolean) = if (isHighContrastPreferred) {
                if (isDarkMode) {
                        if (isPressing) AltPresetColor.shallowDark else AltPresetColor.emphaticDark
                } else {
                        if (isPressing) AltPresetColor.shallowLight else AltPresetColor.emphaticLight
                }
        } else {
                if (isDarkMode) {
                        if (isPressing) PresetColor.shallowDark else PresetColor.emphaticDark
                } else {
                        if (isPressing) PresetColor.shallowLight else PresetColor.emphaticLight
                }
        }

        /** Background color for this input key.
         * @param isDarkMode In night mode color scheme?
         * @param isHighContrastPreferred Prefer high contrast color scheme?
         * @param shouldPreviewKey Display key bubble to preview this key?
         * @param isPressing Is interacting?
         * @return Background color for this input key.
         */
        fun inputKeyBackColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean, shouldPreviewKey: Boolean, isPressing: Boolean): Color = if (isHighContrastPreferred) {
                if (isDarkMode) {
                        if (shouldPreviewKey.not() && isPressing) AltPresetColor.emphaticDark else AltPresetColor.shallowDark
                } else {
                        if (shouldPreviewKey.not() && isPressing) AltPresetColor.emphaticLight else AltPresetColor.shallowLight
                }
        } else {
                if (isDarkMode) {
                        if (shouldPreviewKey.not() && isPressing) PresetColor.emphaticDark else PresetColor.shallowDark
                } else {
                        if (shouldPreviewKey.not() && isPressing) PresetColor.emphaticLight else PresetColor.shallowLight
                }
        }

        /** Shape border color for this key.
         * @param isDarkMode In night mode color scheme?
         * @param isHighContrastPreferred Prefer high contrast color scheme?
         * @return Shape border color for this key.
         */
        fun keyBorderColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean): Color = if (isHighContrastPreferred) {
                if (isDarkMode) Color.White else Color.Black
        } else {
                Color.Transparent
        }

        /** Bubble background color for this input key.
         * @param isDarkMode In night mode color scheme?
         * @param isHighContrastPreferred Prefer high contrast color scheme?
         * @return Bubble background color for this input key.
         */
        fun previewInputKeyBackColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean): Color = if (isHighContrastPreferred) {
                if (isDarkMode) AltPresetColor.shallowDark else AltPresetColor.shallowLight
        } else {
                if (isDarkMode) PresetColor.solidShallowDark else PresetColor.solidShallowLight
        }

        /** Bubble border color for this key.
         * @param isDarkMode In night mode color scheme?
         * @param isHighContrastPreferred Prefer high contrast color scheme?
         * @return Bubble border color for this key.
         */
        fun previewKeyBorderColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean): Color = if (isHighContrastPreferred) {
                if (isDarkMode) Color.White else Color.Black
        } else {
                if (isDarkMode) Color.DarkGray else Color.LightGray
        }
}
