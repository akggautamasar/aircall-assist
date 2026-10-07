package com.akggautamasar.aircallassist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akggautamasar.aircallassist.session.DeviceIdentity
import com.akggautamasar.aircallassist.tts.TtsManager

@Composable
fun AirCallScreen(identity: DeviceIdentity, tts: TtsManager) {
    var callAssist by remember { mutableStateOf(false) }
    var partnerCode by remember { mutableStateOf("") }
    var connected by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("AirCall Assist", style = MaterialTheme.typography.headlineMedium)
        Text("Your lightweight silent-text companion for long-distance calls.")

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Your AirCall code", style = MaterialTheme.typography.titleMedium)
                Text(identity.pairingCode, style = MaterialTheme.typography.headlineSmall)
                Text("Share this code with the person you are talking to.")
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Call Assist", style = MaterialTheme.typography.titleMedium)
                Text(if (callAssist) "Incoming messages will be spoken" else "Off")
            }
            Switch(checked = callAssist, onCheckedChange = { callAssist = it })
        }

        HorizontalDivider()
        Text("Connect", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = partnerCode,
            onValueChange = { partnerCode = it.uppercase().filter { c -> c.isLetterOrDigit() || c == '-' }.take(9) },
            label = { Text("Partner AirCall code") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { connected = partnerCode.length == 9 && partnerCode != identity.pairingCode },
            enabled = !connected,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (connected) "Connected" else "Pair") }

        if (connected) {
            Text("Connected • realtime session", color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Type without speaking…") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { message = "" }, enabled = message.isNotBlank()) { Text("Send") }
                OutlinedButton(onClick = {
                    if (message.isNotBlank()) { tts.speak(message); message = "" }
                }, enabled = message.isNotBlank()) { Text("Test TTS") }
            }
            Text("Transport is the next layer. Messages are designed to be ephemeral, not stored as chat history.")
        }

        OutlinedButton(onClick = { tts.speak("AirCall Assist is ready.") }, Modifier.fillMaxWidth()) {
            Text("Test voice")
        }
    }
}