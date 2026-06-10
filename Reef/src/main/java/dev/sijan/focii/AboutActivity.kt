package dev.sijan.focii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.sijan.focii.screens.AboutScreen
import dev.sijan.focii.ui.ReefTheme

class AboutActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ReefTheme {
                AboutScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}
