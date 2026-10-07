package com.akggautamasar.aircallassist.tts

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private val tts = TextToSpeech(context.applicationContext, this)
    private var ready = false

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            tts.language = Locale.getDefault()
            tts.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
        }
    }

    fun speak(text: String) {
        if (!ready) return
        val normalized = text
            .replace(Regex("[\\p{So}\\p{Cn}]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        if (normalized.isNotBlank()) {
            tts.speak(normalized, TextToSpeech.QUEUE_ADD, null, "aircall-" + System.nanoTime())
        }
    }

    fun setRate(rate: Float) {
        tts.setSpeechRate(rate.coerceIn(0.5f, 2.0f))
    }

    fun stop() { tts.stop() }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}