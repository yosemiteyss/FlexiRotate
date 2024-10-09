package com.yosemiteyss.flexirotate.ui.screen

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.yosemiteyss.flexirotate.R
import com.yosemiteyss.flexirotate.ui.theme.Spacing

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun EnableNotificationPage() {
    val notificationPermissionState =
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Rounded.Notifications,
            contentDescription = stringResource(R.string.permission_notification_reason),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.LARGE))

        Text(
            text = if (notificationPermissionState.status.isGranted) {
                stringResource(R.string.permission_notification_granted)
            } else {
                stringResource(R.string.permission_notification_reason)
            },
            textAlign = TextAlign.Center,
            minLines = 3,
            modifier = Modifier.padding(horizontal = Spacing.LARGE)
        )

        Spacer(modifier = Modifier.height(Spacing.EXTRA_LARGE))

        if (notificationPermissionState.status.isGranted) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = stringResource(R.string.permission_notification_granted),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
        } else {
            Button(onClick = {
                notificationPermissionState.launchPermissionRequest()
            }) {
                Text(text = stringResource(R.string.permission_grant_action))
            }
        }
    }
}
