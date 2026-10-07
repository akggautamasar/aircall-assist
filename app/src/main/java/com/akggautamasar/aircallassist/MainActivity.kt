package com.akggautamasar.aircallassist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akggautamasar.aircallassist.session.DeviceIdentity
import com.akggautamasar.aircallassist.tts.TtsManager
import com.akggautamasar.aircallassist.ui.AirCallScreen

class MainActivity : ComponentActivity() {
    private lateinit var identity: DeviceIdentity
    private lateinit var tts: TtsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }
        identity = DeviceIdentity(this)
        tts = TtsManager(this)
        setContent { AirCallScreen(identity, tts) }
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}