package com.yosemiteyss.flexirotate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yosemiteyss.flexirotate.ui.screen.SetupScreen
import com.yosemiteyss.flexirotate.ui.theme.FlexiRotateTheme

class FoldStateRotateSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FlexiRotateTheme {
                SetupScreen()
            }
        }
    }
}

