package com.akggautamasar.aircallassist.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.akggautamasar.aircallassist.session.DeviceIdentity
import com.akggautamasar.aircallassist.service.CallAssistService
import com.akggautamasar.aircallassist.tts.TtsManager

@Composable
fun AirCallScreen(identity: DeviceIdentity, tts: TtsManager) {
    var callAssist by remember { mutableStateOf(false) }
    var partnerCode by remember { mutableStateOf("") }
    var paired by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    fun startAssist() {
        val intent = Intent(androidx.compose.ui.platform.LocalContext.current, CallAssistService::class.java).apply {
            action = CallAssistService.ACTION_START
            putExtra(CallAssistService.EXTRA_PARTNER, partnerCode)
            putExtra(CallAssistService.EXTRA_SPEAK, true)
        }
        ContextCompat.startForegroundService(androidx.compose.ui.platform.LocalContext.current, intent)
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("AirCall Assist", style = MaterialTheme.typography.headlineMedium)
        Text("Silent text for long-distance calls. No accounts and no chat history.")

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Your AirCall code", style = MaterialTheme.typography.titleMedium)
                Text(identity.pairingCode, style = MaterialTheme.typography.headlineSmall)
                Text("Share this code with your partner.")
            }
        }

        Text("Partner", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = partnerCode,
            onValueChange = { partnerCode = it.uppercase().filter { c -> c.isLetterOrDigit() || c == '-' }.take(9) },
            label = { Text("Partner AirCall code") },
            singleLine = true,
            enabled = !paired,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                paired = partnerCode.length == 9 && partnerCode != identity.pairingCode
                if (paired) startAssist()
            },
            enabled = !paired && partnerCode.length == 9,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (paired) "Paired" else "Pair & Connect") }

        HorizontalDivider()

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Call Assist", style = MaterialTheme.typography.titleMedium)
                Text(if (callAssist) "Speaking incoming messages" else "Off")
            }
            Switch(
                checked = callAssist,
                onCheckedChange = {
                    callAssist = it
                    if (it && paired) startAssist()
                    if (!it) context.stopService(Intent(context, CallAssistService::class.java))
                }
            )
        }

        if (paired) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Type without speaking…") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    context.startService(Intent(context, CallAssistService::class.java).apply {
                        action = CallAssistService.ACTION_SEND
                        putExtra(CallAssistService.EXTRA_TEXT, message)
                    })
                    message = ""
                },
                enabled = message.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Send silently") }
        }

        OutlinedButton(
            onClick = { tts.speak("AirCall Assist is ready.") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Test voice") }

        Text(
            "Messages are realtime and ephemeral. The external call is never intercepted or recorded.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}