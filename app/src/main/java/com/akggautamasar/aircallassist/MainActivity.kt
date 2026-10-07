package com.akggautamasar.aircallassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akggautamasar.aircallassist.tts.TtsManager

class MainActivity : ComponentActivity() {
    private lateinit var ttsManager: TtsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ttsManager = TtsManager(this)
        setContent { AirCallAssistApp(ttsManager) }
    }

    override fun onDestroy() {
        ttsManager.shutdown()
        super.onDestroy()
    }
}

@Composable
private fun AirCallAssistApp(ttsManager: TtsManager) {
    var callAssist by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("AirCall Assist") }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Hands-free messaging while you're on a call.",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                "Call Assist reads eligible AirCall messages aloud to you. " +
                    "It does not intercept or inject audio into another app's call."
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Call Assist", style = MaterialTheme.typography.titleMedium)
                Switch(checked = callAssist, onCheckedChange = { callAssist = it })
                Text(if (callAssist) "Active" else "Off")
            }
            Button(onClick = { ttsManager.speak("AirCall Assist voice test. Call Assist is ready.") }) {
                Text("Test voice")
            }
        }
    }
}
