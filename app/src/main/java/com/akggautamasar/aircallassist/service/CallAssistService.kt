package com.akggautamasar.aircallassist.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.akggautamasar.aircallassist.BuildConfig
import com.akggautamasar.aircallassist.session.DeviceIdentity
import com.akggautamasar.aircallassist.transport.RealtimeClient
import com.akggautamasar.aircallassist.tts.TtsManager

class CallAssistService : Service() {
    private lateinit var identity: DeviceIdentity
    private lateinit var tts: TtsManager
    private var client: RealtimeClient? = null
    private var enabled = false
    private var partnerCode = ""

    override fun onCreate() {
        super.onCreate()
        identity = DeviceIdentity(this)
        tts = TtsManager(this)
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                enabled = intent.getBooleanExtra(EXTRA_SPEAK, true)
                partnerCode = intent.getStringExtra(EXTRA_PARTNER) ?: ""
                startForeground(NOTIFICATION_ID, notification("Call Assist active"))
                connect()
            }
            ACTION_SEND -> {
                intent.getStringExtra(EXTRA_TEXT)?.takeIf { it.isNotBlank() }?.let { client?.send(it, true) }
            }
            ACTION_STOP -> stopSelf()
        }
        return START_STICKY
    }

    private fun connect() {
        client?.close()
        client = RealtimeClient(
            deviceId = identity.deviceId,
            pairingCode = identity.pairingCode,
            relayUrl = BuildConfig.RELAY_URL,
            onMessage = { message -> if (enabled && message.speak) tts.speak(message.text) },
            onStatus = { status -> updateNotification(status) }
        ).also {
            it.connect()
            if (partnerCode.isNotBlank()) it.pairWith(partnerCode)
        }
    }

    private fun createChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "AirCall Assist", NotificationManager.IMPORTANCE_LOW)
        )
    }

    private fun notification(text: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("AirCall Assist")
            .setContentText(text)
            .setOngoing(true)
            .build()

    private fun updateNotification(text: String) {
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification(text))
    }

    override fun onDestroy() {
        client?.close()
        tts.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "com.akggautamasar.aircallassist.START"
        const val ACTION_SEND = "com.akggautamasar.aircallassist.SEND"
        const val ACTION_STOP = "com.akggautamasar.aircallassist.STOP"
        const val EXTRA_PARTNER = "partnerCode"
        const val EXTRA_SPEAK = "speak"
        const val EXTRA_TEXT = "text"
        private const val CHANNEL_ID = "aircall-assist"
        private const val NOTIFICATION_ID = 1001
    }
}