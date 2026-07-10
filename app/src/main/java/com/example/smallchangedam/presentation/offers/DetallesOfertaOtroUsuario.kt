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
import com.example.smallchangedam.data.ApiService
import com.example.smallchangedam.data.OfertaResponse // Asegúrate de importar tu modelo real
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val ColorMarron = Color(0xFFB08968)
private val ColorGrisClaro = Color(0xFFD9D9D9)
private val ColorVerdeTag = Color(0xFF72C075)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesOfertaOtroUsuario(
    navController: NavController,
    ofertaId: Int,
    apiService: ApiService // Inyecta o pasa la instancia de tu API Service aquí
) {
    // 1. Definición de los estados de la pantalla
    var oferta by remember { mutableStateOf<OfertaResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 2. Llamada asíncrona a la API al cargar la pantalla
    LaunchedEffect(ofertaId) {
        try {
            isLoading = true
            errorMessage = null
            // Cambiamos al hilo de I/O para llamadas de red
            val response = withContext(Dispatchers.IO) {
                apiService.obtenerOfertaPorId(ofertaId)
            }
            oferta = response
        } catch (e: Exception) {
            errorMessage = "Error al cargar la oferta: ${e.localizedMessage ?: "Conexión fallida"}"
        } finally {
            isLoading = false
        }
    }

    val curveShape = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height * 0.75f)
        quadraticTo(size.width / 2f, size.height, 0f, size.height * 0.75f)
        close()
    }

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

        // 3. Manejo de estados en la UI
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorMarron)
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(16.dp))
                }
            }
            oferta != null -> {
                val datosOferta = oferta!!

                val montoADar = datosOferta.cantidad
                val tc = datosOferta.tipoCambio

                val totalRecibido = montoADar * tc
                val totalFormateado = "${String.format("%.2f", totalRecibido)} ${datosOferta.monedaAEnviar}"

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color.White)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header curvo con los datos del usuario real obtenidos de tu OfertaResponse
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

                            // Usamos 'nombreUsuario' de tu archivo Kotlin
                            Text(
                                text = datosOferta.nombreUsuario ?: "Usuario #${datosOferta.clienteId}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))

                                // Usamos 'calificacionUsuario' de tu archivo Kotlin
                                Text(
                                    text = (datosOferta.calificacionUsuario ?: 0.0).toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFC107)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Detalles en el formulario
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Como tú eres el usuario que toma la oferta del otro, las monedas se invierten:
                        // Lo que el creador de la oferta envía (monedaAEnviar), es lo que TÚ recibes.
                        // Lo que el creador recibe (monedaARecibir), es lo que TÚ das.
                        DetalleItem(label = "Tú recibes:", value = "$montoADar ${datosOferta.monedaARecibir}")
                        DetalleItem(label = "Tú das:", value = totalFormateado)
                        DetalleItem(label = "Fecha de Publicación:", value = datosOferta.fechaCreacion.take(10))

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
                                    text = "$tc ${datosOferta.monedaAEnviar}/${datosOferta.monedaARecibir}",
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Acción para iniciar la transacción
                        var montoSeleccionado = 6769.0
                        Button(
                            onClick = {
                                montoSeleccionado = totalRecibido
                                // Aquí podrías navegar a una pantalla de confirmación o ejecutar el endpoint `crearTransaccion`
                                navController.navigate("payment_gateway/$montoSeleccionado")
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
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 0.5.dp,
            color = Color.LightGray
        )
    }
}