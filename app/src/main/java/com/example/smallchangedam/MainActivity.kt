package com.example.smallchangedam

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import com.example.smallchangedam.data.RetrofitClient
import com.example.smallchangedam.presentation.navegation.AppNavGraph
import com.example.smallchangedam.ui.theme.SmallChangeDAMTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmallChangeDAMTheme {

                // Prueba de fuego de la API al arrancar la app
                LaunchedEffect(key1 = true) {
                    try {
                        // Cambia a un hilo secundario para no trabar la interfaz
                        withContext(Dispatchers.IO) {
                            // Intentamos pedir la lista de ofertas al backend de Visual Studio
                            val ofertas = RetrofitClient.apiService.listarOfertas()
                            Log.d("API_TEST", "¡Conexión Exitosa! Número de ofertas encontradas: ${ofertas.size}")
                        }
                    } catch (e: Exception) {
                        Log.e("API_TEST", "Error al conectar con la API de Visual Studio", e)
                    }
                }

                AppNavGraph()
            }
        }
    }
}