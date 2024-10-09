package com.yosemiteyss.flexirotate.ui.screen

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.yosemiteyss.flexirotate.R
import com.yosemiteyss.flexirotate.ui.theme.Spacing

@Composable
fun EnableWriteSettingsPage() {
    val context = LocalContext.current

    val isWritingSettingsGranted = remember { mutableStateOf(Settings.System.canWrite(context)) }

    fun onRequestWriteSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Rounded.SettingsApplications,
            contentDescription = stringResource(R.string.permission_write_settings_reason),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.LARGE))

        Text(
            text = if (isWritingSettingsGranted.value) {
                stringResource(R.string.permission_write_settings_granted)
            } else {
                stringResource(R.string.permission_write_settings_reason)
            },
            textAlign = TextAlign.Center,
            minLines = 3,
            modifier = Modifier.padding(horizontal = Spacing.LARGE)
        )

        Spacer(modifier = Modifier.height(Spacing.EXTRA_LARGE))

        if (isWritingSettingsGranted.value) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = stringResource(R.string.permission_write_settings_granted),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
        } else {
            Button(onClick = ::onRequestWriteSettings) {
                Text(text = stringResource(R.string.permission_grant_action))
            }
        }
    }
}