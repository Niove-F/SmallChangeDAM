package com.example.smallchangedam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smallchangedam.presentation.home.HomeScreen
import com.example.smallchangedam.ui.theme.SmallChangeDAMTheme
import com.example.smallchangedam.presentation.navegation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmallChangeDAMTheme {
                AppNavGraph()

                //Scaffold(modifier = Modifier.fillMaxSize())
                //{
                        //innerPadding -> AppNavGraph()
                //}
            }
        }
    }
}