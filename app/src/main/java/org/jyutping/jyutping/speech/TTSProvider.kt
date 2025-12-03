package org.jyutping.jyutping.speech

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jyutping.jyutping.presets.PresetConstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TTSProvider(private val context: Context?) : TextToSpeech.OnInitListener {

        private val logTag: String = PresetConstant.keyboardPackageName + ".tts"

        private var tts: TextToSpeech? = null

        private val preferredLocale = Locale.Builder()
                .setLanguage("yue")
                .setScript("Hant")
                .setRegion("HK")
                .build()

        private val _isReady = MutableStateFlow(false)
        val isReady = _isReady.asStateFlow()
        private val _isCantoneseSupported = MutableStateFlow(false)
        val isCantoneseSupported = _isCantoneseSupported.asStateFlow()

        fun initialize() {
                if (context == null) {
                        Log.e(logTag, "Context is null, cannot initialize TTS.")
                        return
                }
                if (tts != null) {
                        Log.i(logTag, "TTS is already initialized.")
                        return
                }
                if (Looper.myLooper() == Looper.getMainLooper()) {
                        performInitialization()
                } else {
                        Handler(Looper.getMainLooper()).post { performInitialization() }
                }
        }
        private fun performInitialization() {
                try {
                        tts = TextToSpeech(context, this)
                } catch (t: Throwable) {
                        Log.e(logTag, "Failed to initialize TextToSpeech engine.", t)
                        _isReady.value = false
                        _isCantoneseSupported.value = false
                }
        }

        override fun onInit(status: Int) {
                if (status == TextToSpeech.SUCCESS) {
                        if (tts == null) return
                        try {
                                val result = tts?.isLanguageAvailable(preferredLocale)
                                when (result) {
                                        TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE,
                                        TextToSpeech.LANG_COUNTRY_AVAILABLE,
                                        TextToSpeech.LANG_AVAILABLE -> {
                                                tts?.language = preferredLocale
                                                tts?.setSpeechRate(0.85f)
                                                _isCantoneseSupported.value = true
                                        }
                                        else -> {
                                                Log.e(logTag, "Cantonese TTS is not supported.")
                                                tts?.language = Locale.getDefault()
                                                _isCantoneseSupported.value = false
                                        }
                                }
                                _isReady.value = true
                        } catch (t: Throwable) {
                                Log.e(logTag, "Error during onInit configuration.", t)
                                _isReady.value = false
                        }
                } else {
                        Log.e(logTag, "TTS Initialization failed.")
                        _isReady.value = false
                }
        }

        @OptIn(ExperimentalUuidApi::class)
        fun speak(text: String) {
                if (_isReady.value && _isCantoneseSupported.value) {
                        val utteranceId = Uuid.random().toString()
                        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                        if (result == TextToSpeech.ERROR) {
                                Log.e(logTag, "Failed to speak text: $text")
                        }
                }
        }

        @OptIn(ExperimentalUuidApi::class)
        fun ssmlSpeak(romanization: String, cantonese: String? = null) {
                val fallback: String = cantonese ?: romanization

                val text = """
                        <speak>
                            <phoneme alphabet="jyutping" ph="$romanization">$fallback</phoneme>
                        </speak>
                """.trimIndent()

                if (_isReady.value && _isCantoneseSupported.value) {
                        val utteranceId = Uuid.random().toString()
                        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                        if (result == TextToSpeech.ERROR) {
                                Log.e(logTag, "Failed to speak text: $romanization")
                        }
                }
        }

        fun shutdown() {
                if (_isReady.value) {
                        try {
                                tts?.stop()
                                tts?.shutdown()
                        } catch (t: Throwable) {
                                Log.e(logTag, "Error during TTS shutdown.", t)
                        } finally {
                                tts = null
                                _isReady.value = false
                                _isCantoneseSupported.value = false
                        }
                }
        }
}
