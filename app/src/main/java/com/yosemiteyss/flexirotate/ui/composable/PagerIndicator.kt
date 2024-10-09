package com.yosemiteyss.flexirotate.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.yosemiteyss.flexirotate.ui.theme.Spacing

@Composable
fun PagerIndicator(count: Int, currentIndex: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = Spacing.LARGE),
    ) {
        repeat(count) { iteration ->
            val color = if (currentIndex == iteration)
                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer

            Box(
                modifier = Modifier
                    .padding(Spacing.SMALL)
                    .background(color = color, shape = CircleShape)
                    .size(8.dp)
            )
        }
    }
}