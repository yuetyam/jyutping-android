package org.jyutping.jyutping.speech

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.LocalTTSProvider

@Composable
fun Speaker(romanization: String? = null, cantonese: String? = null) {
        val ttsProvider = LocalTTSProvider.current
        val isReady by ttsProvider.isReady.collectAsState()
        val isCantoneseSupported by ttsProvider.isCantoneseSupported.collectAsState()
        Button(
                onClick = {
                        if (romanization != null) {
                                ttsProvider.ssmlSpeak(romanization = romanization, cantonese = cantonese)
                        } else if (cantonese != null) {
                                ttsProvider.speak(cantonese)
                        }
                },
                modifier = Modifier.size(32.dp),
                enabled = isReady && isCantoneseSupported,
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
        ) {
                Icon(
                        imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                )
        }
}
