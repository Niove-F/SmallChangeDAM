package com.example.smallchangedam.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.smallchangedam.presentation.components.CustomBottomBar
import com.example.smallchangedam.data.OfertaResponse
import com.example.smallchangedam.data.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class OfertaUI(
    val id: Int,
    val usuario: String,
    val calificacion: Double,
    val monedaADar: String,
    val monedaARecibir: String,
    val monto: String,
    val tc: String,
    val tiempo: String
)

// COLORESS
val ColorMarron = Color(0xFFB08968)
val ColorGrisFondo = Color(0xFFE0E0E0)
val ColorBlancoFondo = Color(0xFFF8F9FA)
val ColorVerdeTag = Color(0xFF72C075)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var ofertasTotales by remember { mutableStateOf<List<OfertaUI>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var itemsVisibles by remember { mutableIntStateOf(10) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val response = RetrofitClient.apiService.listarOfertas()

            ofertasTotales = response.map { ofertaBackend ->
                OfertaUI(
                    id = ofertaBackend.id,
                    usuario = ofertaBackend.nombreUsuario ?: "Usuario #${ofertaBackend.clienteId}",
                    calificacion = ofertaBackend.calificacionUsuario ?: 0.0,
                    monedaADar = ofertaBackend.monedaAEnviar,
                    monedaARecibir = ofertaBackend.monedaARecibir,
                    monto = ofertaBackend.cantidad.toString(),
                    tc = ofertaBackend.tipoCambio.toString(),
                    tiempo = calcularTiempoTranscurrido(ofertaBackend.fechaCreacion)
                )
            }.sortedByDescending { it.id } // Ordenamos por las más recientes (ID mayor)

        } catch (e: Exception) {
            errorMessage = "Error al cargar las ofertas: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    val reachedBottom: Boolean by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != 0 && lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom && itemsVisibles < ofertasTotales.size) {
            itemsVisibles += 10
        }
    }

Box(modifier = Modifier.fillMaxSize()) {
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
                        Icon(
                            Icons.Default.AccountCircle, 
                            contentDescription = "Perfil", 
                            tint = Color.White
                        )
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
                "f8" -> .fillMaxSize()
                .padding(paddingValues)
                .background(ColorBlancoFondo)
        ) {
            FiltrosSeccion()

            Row(
                modifier = Modifier
                    .fillMaxSize()
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

                // Manejo de Estados de la Lista de Ofertas
                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ColorMarron)
                        }
                    }
                    errorMessage != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(16.dp))
                        }
                    }
                    ofertasTotales.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Aún no hay ofertas publicadas.", color = Color.Gray)
                        }
                    }
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val ofertasMostrar = ofertasTotales.take(itemsVisibles)

                            items(ofertasMostrar) { oferta ->
                                TarjetaOferta(oferta, navController)
                            }
                            
                            // Indicador de carga cuando hace scroll hacia abajo
                            if (itemsVisibles < ofertasTotales.size) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp), 
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = ColorMarron)
                                    }
                                }
                            }
                        }
                    }
                }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        CustomBottomBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter) // Se ancla al fondo
        )
    }
}
fun calcularTiempoTranscurrido(fechaIso: String): String {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse(fechaIso) ?: return "Hace un momento"

        val diffMilisegundos = Date().time - date.time
        val minutos = diffMilisegundos / (1000 * 60)
        val horas = minutos / 60
        val dias = horas / 24

        when {
            dias > 0 -> "Hace $dias d"
            horas > 0 -> "Hace $horas h"
            minutos > 0 -> "Hace $minutos min"
            else -> "Hace un momento"
        }
    } catch (e: Exception) {
        "Fecha desconocida"
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
fun TarjetaOferta(oferta: OfertaUI, navController: NavController) {
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
                text = "Tú das: $totalFormateado  |  Tú recibes: ${oferta.monto} ${oferta.monedaADar}",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

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
                        text = "TC: ${oferta.tc} ${oferta.monedaARecibir}/${oferta.monedaADar}",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

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