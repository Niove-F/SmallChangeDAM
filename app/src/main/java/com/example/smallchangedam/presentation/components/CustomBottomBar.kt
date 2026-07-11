package com.example.smallchangedam.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CustomBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp, start = 30.dp, end = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(50.dp)),
            // 0xE0E0E0 es el color, y el primer 80 es el alfa/transparencia
            color = Color(0xFFE0E0E0),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cambiamos el tinte a un color oscuro para que resalte sobre el fondo gris claro
                val colorIcono = Color(0xFF333333)

                BotonIsla("T.C.", Icons.Default.DateRange, colorIcono) { navController.navigate("tipoCambio") }
                BotonIsla("Mis Ofertas", Icons.AutoMirrored.Filled.List, colorIcono) { navController.navigate("misOfertas") }
                BotonIsla("Publicar", Icons.Default.AddCircle, colorIcono) { navController.navigate("publicarOferta") }
            }
        }
    }
}

@Composable
fun BotonIsla(texto: String, icono: ImageVector, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(icono, contentDescription = texto, tint = color, modifier = Modifier.size(24.dp))
        Text(text = texto, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}