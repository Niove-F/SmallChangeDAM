package com.example.smallchangedam.presentation.offers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun publishOffer(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Texto llamativo para saber que entramos a la pantalla correcta
        Text(
            text = "✨ Pantalla de Publicar Oferta ✨",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB08968) // El color marrón de tu app
        )

        Text(
            text = "¡La navegación está funcionando perfectamente!",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Botón para probar el regreso a la pantalla anterior
        Button(
            onClick = {
                // popBackStack() destruye esta pantalla y te regresa al Home
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Volver al Home", color = Color.White)
        }
    }
}