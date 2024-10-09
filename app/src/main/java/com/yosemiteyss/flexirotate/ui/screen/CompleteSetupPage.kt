package com.yosemiteyss.flexirotate.ui.screen

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
import com.yosemiteyss.flexirotate.R.*
import com.yosemiteyss.flexirotate.ui.theme.Spacing
import com.yosemiteyss.flexirotate.utils.findActivity

@Composable
fun CompleteSetupPage() {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Rounded.CheckCircleOutline,
            contentDescription = stringResource(string.setup_completed),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.LARGE))

        Text(
            text = stringResource(string.setup_completed),
            textAlign = TextAlign.Center,
            minLines = 3,
            modifier = Modifier.padding(horizontal = Spacing.LARGE)
        )

        Spacer(modifier = Modifier.height(Spacing.EXTRA_LARGE))


        Button(onClick = { context.findActivity()?.finish() }) {
            Text(text = stringResource(string.setup_completed_exit))
        }
    }
}