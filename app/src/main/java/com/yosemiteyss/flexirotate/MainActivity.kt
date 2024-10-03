package com.yosemiteyss.flexirotate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yosemiteyss.flexirotate.ui.theme.FlexiRotateTheme

class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FlexiRotateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    if (Settings.System.canWrite(this)) {
                        Toast.makeText(this, "WRITE_SETTINGS granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "WRITE_SETTINGS not granted", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        requestWriteSettingsPermission()
    }

    private fun requestWriteSettingsPermission() {
        if (!Settings.System.canWrite(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:${packageName}")
            permissionLauncher.launch(intent)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlexiRotateTheme {
        Greeting("Android")
    }
}