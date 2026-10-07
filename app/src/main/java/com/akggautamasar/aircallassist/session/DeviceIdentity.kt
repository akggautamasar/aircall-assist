package com.akggautamasar.aircallassist.session

import android.content.Context
import java.security.SecureRandom

class DeviceIdentity(context: Context) {
    private val prefs = context.getSharedPreferences("aircall_identity", Context.MODE_PRIVATE)

    val deviceId: String get() = prefs.getString("device_id", null) ?: buildId().also { prefs.edit().putString("device_id", it).apply() }
    val pairingCode: String get() = prefs.getString("pairing_code", null) ?: buildCode().also { prefs.edit().putString("pairing_code", it).apply() }

    fun regeneratePairingCode(): String = buildCode().also { prefs.edit().putString("pairing_code", it).apply() }

    private fun buildId() = ByteArray(16).also { SecureRandom().nextBytes(it) }.joinToString("") { "%02x".format(it) }

    private fun buildCode(): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val random = SecureRandom()
        val raw = buildString { repeat(8) { append(alphabet[random.nextInt(alphabet.length)]) } }
        return raw.substring(0, 4) + "-" + raw.substring(4)
    }
}
