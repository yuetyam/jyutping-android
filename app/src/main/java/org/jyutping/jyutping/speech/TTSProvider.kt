package org.jyutping.jyutping.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jyutping.jyutping.presets.PresetConstant

class TTSProvider(context: Context) : TextToSpeech.OnInitListener {

        private var tts: TextToSpeech? = TextToSpeech(context, this)

        private val cantonese = Locale.Builder()
                .setLanguage("yue")
                .setScript("Hant")
                .setRegion("HK")
                .build()

        private val _isReady = MutableStateFlow(false)
        val isReady = _isReady.asStateFlow()
        private val _isCantoneseSupported = MutableStateFlow(false)
        val isCantoneseSupported = _isCantoneseSupported.asStateFlow()

        val logTag: String = PresetConstant.keyboardPackageName + ".tts"

        override fun onInit(status: Int) {
                if (status == TextToSpeech.SUCCESS) {
                        val result = tts?.isLanguageAvailable(cantonese)
                        when (result) {
                                TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE,
                                TextToSpeech.LANG_COUNTRY_AVAILABLE -> {
                                        tts?.language = cantonese
                                        _isCantoneseSupported.value = true
                                }
                                else -> {
                                        _isCantoneseSupported.value = false
                                        Log.e(logTag, "Cantonese TTS is not supported.")
                                }
                        }
                        _isReady.value = true
                } else {
                        Log.e(logTag, "TTS Initialization failed.")
                        _isReady.value = false
                }
        }

        fun speak(text: String) {
                if (_isReady.value && _isCantoneseSupported.value) {
                        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                        if (result == TextToSpeech.ERROR) {
                                Log.e(logTag, "Failed to speak text: $text")
                        }
                }
        }

        fun ssmlSpeak(romanization: String, cantonese: String? = null) {
                val fallback: String = cantonese ?: romanization

                val text = """
                        <speak>
                            <phoneme alphabet="jyutping" ph="$romanization">$fallback</phoneme>
                        </speak>
                """.trimIndent()

                if (_isReady.value && _isCantoneseSupported.value) {
                        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                        if (result == TextToSpeech.ERROR) {
                                Log.e(logTag, "Failed to speak text: $romanization")
                        }
                }
        }

        fun shutdown() {
                tts?.stop()
                tts?.shutdown()
        }
}
