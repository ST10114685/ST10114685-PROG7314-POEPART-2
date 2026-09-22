package com.example.deliberate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.deliberate.ui.DeliberateApp
import com.example.deliberate.ui.theme.DeliberateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeliberateTheme {
                DeliberateApp()
            }
        }
    }
}
