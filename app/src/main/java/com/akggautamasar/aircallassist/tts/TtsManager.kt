package com.akggautamasar.aircallassist.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private val tts = TextToSpeech(context.applicationContext, this)
    private var ready = false

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        if (ready) tts.language = Locale.getDefault()
    }

    fun speak(text: String) {
        if (!ready) return
        val normalized = normalize(text)
        if (normalized.isBlank()) return
        tts.speak(
            normalized,
            TextToSpeech.QUEUE_ADD,
            null,
            "aircall-" + System.nanoTime()
        )
    }

    private fun normalize(input: String): String =
        input
            .replace(Regex("[\\p{So}\\p{Cn}]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
