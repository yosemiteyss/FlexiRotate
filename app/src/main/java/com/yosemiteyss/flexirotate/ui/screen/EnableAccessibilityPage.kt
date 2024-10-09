package com.yosemiteyss.flexirotate.ui.screen

import android.content.Intent
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
import com.yosemiteyss.flexirotate.service.FoldStateRotateService
import com.yosemiteyss.flexirotate.R
import com.yosemiteyss.flexirotate.ui.theme.Spacing
import com.yosemiteyss.flexirotate.utils.AccessibilityUtils

@Composable
fun EnableAccessibilityPage() {
    val context = LocalContext.current

    val isAccessibilityServiceEnabled = remember {
        mutableStateOf(
            AccessibilityUtils.isAccessibilityServiceEnabled(
                context,
                FoldStateRotateService::class.java
            )
        )
    }

    fun onOpenAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Rounded.Accessibility,
            contentDescription = stringResource(R.string.accessibility_reason),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.LARGE))

        Text(
            text = if (isAccessibilityServiceEnabled.value) {
                stringResource(R.string.accessibility_enabled)
            } else {
                stringResource(R.string.accessibility_reason)
            },
            minLines = 3,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.LARGE)
        )

        Spacer(modifier = Modifier.height(Spacing.EXTRA_LARGE))

        if (isAccessibilityServiceEnabled.value) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = stringResource(R.string.accessibility_enabled),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
        } else {
            Button(onClick = ::onOpenAccessibilitySettings) {
                Text(text = stringResource(R.string.accessibility_enable_action))
            }
        }
    }
}