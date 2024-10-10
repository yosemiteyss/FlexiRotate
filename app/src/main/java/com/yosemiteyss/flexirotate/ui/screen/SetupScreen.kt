package com.yosemiteyss.flexirotate.ui.screen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.yosemiteyss.flexirotate.R
import com.yosemiteyss.flexirotate.ui.theme.Spacing

@Composable
private fun SetupListRow(
    title: String,
    description: String,
    isDone: Boolean,
    isButtonEnabled: Boolean = true,
    onButtonPressed: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.LARGE, vertical = Spacing.MEDIUM)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.MEDIUM))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(Spacing.LARGE))

        Box(
            modifier = Modifier.width(72.dp)
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Rounded.Done,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            } else {
                Button(
                    onClick = onButtonPressed,
                    enabled = isButtonEnabled,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(text = stringResource(R.string.action_go))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SetupScreen(viewModel: SetupViewModel = SetupViewModel()) {
    val context = LocalContext.current

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    val setupSteps by viewModel.setupSteps.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refreshSetupSteps(context)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_setup),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                item {
                    val notificationPermissionState =
                        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

                    SetupListRow(
                        title = stringResource(R.string.permission_notification_title),
                        description = stringResource(R.string.permission_notification_description),
                        isDone = setupSteps.isNotificationEnabled,
                        onButtonPressed = {
                            notificationPermissionState.launchPermissionRequest()
                        }
                    )
                }
            }

            item {
                SetupListRow(
                    title = stringResource(R.string.modify_write_settings_title),
                    description = stringResource(R.string.modify_write_settings_description),
                    isDone = setupSteps.isWriteSettingsEnabled,
                    onButtonPressed = {
                        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                        intent.data = Uri.parse("package:${context.packageName}")
                        context.startActivity(intent)
                    }
                )
            }

            item {
                SetupListRow(
                    title = stringResource(R.string.ignore_battery_optimization_title),
                    description = stringResource(R.string.ignore_battery_optimization_description),
                    isDone = setupSteps.isBatteryOptimizationIgnored,
                    onButtonPressed = {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        intent.data = Uri.parse("package:${context.packageName}")
                        context.startActivity(intent)
                    }
                )
            }

            item {
                SetupListRow(
                    title = stringResource(R.string.enable_accessibility_title),
                    description = stringResource(R.string.enable_accessibility_description),
                    isDone = setupSteps.isAccessibilityEnabled,
                    isButtonEnabled = setupSteps.canEnableAccessibility,
                    onButtonPressed = {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}
