package com.example.smallchangedam.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Oferta(
    val id: Int,
    val usuario: String,
    val calificacion: Double,
    val monedaADar: String,
    val monedaARecibir: String,
    val monto: String,
    val tc: String,
    val tiempo: String
)

//COLORESS (por ahora)
val ColorMarron = Color(0xFFB08968)
val ColorGrisFondo = Color(0xFFE0E0E0)
val ColorBlancoFondo = Color(0xFFF8F9FA)
val ColorVerdeTag = Color(0xFF72C075)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // Datos de prueba basados en tu captura
    val listaOfertas = listOf(
        Oferta(1, "Emma R.", 3.9, "USD", "PEN", "150", "3.76", "Hace 5 min"),
        Oferta(2, "Martín Perez", 4.1, "EUR", "PEN", "300", "3.98", "Hace 2 horas"),
        Oferta(3, "Rosa Rosales", 3.8, "PEN", "USD", "500", "0.26", "Hace 4 min")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SMALL CHANGE - Ofertas",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("configUser") }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorMarron)
            )
        },
        bottomBar = {
            BottomBarSeccion(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ColorBlancoFondo)
        ) {
            // 1. Sección de Filtros (Fondo Gris)
            FiltrosSeccion()

            // Ordenar por...
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Ordenar por: ", fontSize = 14.sp, color = Color.Gray)
            }

            // 2. Lista de Ofertas
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaOfertas) { oferta ->
                    TarjetaOferta(oferta, navController)
                }
            }
        }
    }
}

@Composable
fun FiltrosSeccion() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorGrisFondo)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FiltroDropdown(text = "Todas las monedas", modifier = Modifier.weight(1f))
            FiltroDropdown(text = "Compra/Venta", modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FiltroInput(placeholder = "Monto mínimo", modifier = Modifier.weight(1f))
            FiltroInput(placeholder = "Monto máximo", modifier = Modifier.weight(1f))
        }
        FiltroDropdown(text = "Fecha: Más reciente", modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun FiltroDropdown(text: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, fontSize = 12.sp, color = Color.Gray)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun FiltroInput(placeholder: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.height(40.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = placeholder, fontSize = 12.sp, color = Color.LightGray)
        }
    }
}

@Composable
fun TarjetaOferta(oferta: Oferta, navController: NavController) {
    val montoNumerico = oferta.monto.toDoubleOrNull() ?: 0.0
    val tcNumerico = oferta.tc.toDoubleOrNull() ?: 0.0
    val totalRecibido = montoNumerico * tcNumerico
    val totalFormateado = "${String.format("%.2f", totalRecibido)} ${oferta.monedaARecibir}"

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("detallesOferta/${oferta.id}") }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior: Usuario y Calificación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.navigate( "perfil/${oferta.usuario}/${oferta.calificacion}" ) } ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = oferta.usuario, fontWeight = FontWeight.Bold, fontSize = 16.sp, )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = oferta.calificacion.toString(), fontWeight = FontWeight.Bold, color = Color(0xFFFFC107))
                }
            }

            Text(
                text = "Tú das: ${oferta.monto} ${oferta.monedaADar}  |  Tú recibes: $totalFormateado",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Fila central: Etiqueta T.C.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorVerdeTag)
                ) {
                    Text(
                        text = "T.C.: ${oferta.tc}",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

            // Fila inferior: Tiempo transcurrido y botón Intercambiar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = oferta.tiempo, color = Color.Gray, fontSize = 12.sp)

                Button(
                    onClick = { navController.navigate("detallesOferta/${oferta.id}") },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorMarron),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = "Intercambiar", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun BottomBarSeccion(navController: NavController) {
    Surface(
        color = ColorMarron,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { navController.navigate("tipoCambio") }, colors = ButtonDefaults.buttonColors(containerColor = Color.White), shape = RoundedCornerShape(8.dp)) {
                Text("Tipo de Cambio", color = Color.Black, fontSize = 12.sp)
            }
            Button(onClick = { navController.navigate("misOfertas") }, colors = ButtonDefaults.buttonColors(containerColor = Color.White), shape = RoundedCornerShape(8.dp)) {
                Text("Mis Ofertas", color = Color.Black, fontSize = 12.sp)
            }
            Button(onClick = { navController.navigate("publicarOferta") }, colors = ButtonDefaults.buttonColors(containerColor = Color.White), shape = RoundedCornerShape(8.dp)) {
                Text("Publicar Oferta", color = Color.Black, fontSize = 12.sp)
            }
        }
    }
}
