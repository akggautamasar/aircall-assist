package com.akggautamasar.aircallassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.akggautamasar.aircallassist.session.DeviceIdentity
import com.akggautamasar.aircallassist.tts.TtsManager
import com.akggautamasar.aircallassist.ui.AirCallScreen

class MainActivity : ComponentActivity() {
    private lateinit var identity: DeviceIdentity
    private lateinit var tts: TtsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        identity = DeviceIdentity(this)
        tts = TtsManager(this)
        setContent { AirCallScreen(identity, tts) }
    }
    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}