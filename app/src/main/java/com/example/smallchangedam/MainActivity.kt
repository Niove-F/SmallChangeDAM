package com.example.smallchangedam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.smallchangedam.navigation.AppNavigation
import com.example.smallchangedam.ui.theme.SmallChangeDAMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmallChangeDAMTheme {
                AppNavigation()
            }
        }
    }
}
