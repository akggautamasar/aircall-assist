package com.akggautamasar.aircallassist.model

data class PairedDevice(val id: String, val displayCode: String)
data class ChatMessage(val id: String, val senderId: String, val text: String, val createdAtEpochMs: Long = System.currentTimeMillis(), val speak: Boolean = true)
data class CallAssistSettings(val enabled: Boolean = false, val speakOnlyDuringCall: Boolean = true, val trustedOnly: Boolean = true, val speechRate: Float = 1.0f)
