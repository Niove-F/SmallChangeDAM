package com.example.smallchangedam.presentation.offers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smallchangedam.presentation.home.Oferta

private val ColorMarron = Color(0xFFB08968)
private val ColorGrisClaro = Color(0xFFD9D9D9)
private val ColorVerdeTag = Color(0xFF72C075)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesOfertaOtroUsuario(navController: NavController, ofertaId: Int) {
    val mockupOferta = when (ofertaId) {
        1 -> Oferta(1, "Emma R.", 3.9, "USD", "PEN", "150", "3.76", "Hace 5 min")
        2 -> Oferta(2, "Martín Perez", 4.1, "EUR", "PEN", "300", "3.98", "Hace 2 horas")
        else -> Oferta(3, "Rosa Rosales", 3.8, "PEN", "USD", "500", "0.26", "Hace 4 min")
    }

    val curveShape = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height * 0.75f)
        quadraticTo(
            size.width / 2f, size.height,
            0f, size.height * 0.75f
        )
        close()
    }

    // Cálculo del total para el mockup
    val montoNumerico = mockupOferta.monto.toDoubleOrNull() ?: 0.0
    val tcNumerico = mockupOferta.tc.toDoubleOrNull() ?: 0.0
    val totalRecibido = montoNumerico * tcNumerico
    val totalFormateado = "${String.format("%.2f", totalRecibido)} ${mockupOferta.monedaARecibir}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de Oferta", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorMarron)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Header curvo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(ColorGrisClaro, shape = curveShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mockupOferta.usuario,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = mockupOferta.calificacion.toString(),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFC107)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Detalles de la oferta mockup
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetalleItem(label = "Tú das:", value = "${mockupOferta.monto} ${mockupOferta.monedaADar}")
                DetalleItem(label = "Tú recibes:", value = totalFormateado)
                DetalleItem(label = "Tiempo:", value = mockupOferta.tiempo)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tasa:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ColorVerdeTag)
                    ) {
                        Text(
                            text = "${mockupOferta.tc} ${mockupOferta.monedaARecibir}/${mockupOferta.monedaADar}",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorMarron),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "INTERCAMBIAR AHORA",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DetalleItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(top = 4.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
    }
}
