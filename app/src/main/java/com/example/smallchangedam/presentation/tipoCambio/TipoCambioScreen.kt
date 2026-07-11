package com.example.smallchangedam.presentation.tipoCambio

import android.R.color.white
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Locale


private val ColorVerdeTag = Color(0xFF72C075)
val BrownTheme = Color(0xFFA67C52)
val LightBackground = Color(0xFFFAFAFA)
val AlertBackground = Color(0xFFF7F2F2)
val AlertRed = Color(0xFFB71C1C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoCambioScreen(
    viewModel: TipoCambioViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    // Equivalente a propiedades computed: Calcula el resultado reactivamente
    val cantidadDouble = uiState.cantidad.toDoubleOrNull() ?: 0.0
    val cantidadConvertida = remember(cantidadDouble, uiState.tasaActual) {
        if (uiState.cantidad.isEmpty()) "" else String.format(Locale.US, "%.4f", cantidadDouble * uiState.tasaActual)
    }
    val tasaFormateada = remember(uiState.tasaActual) {
        String.format(Locale.US, "%.5f", uiState.tasaActual)
    }
    val tasaInversaFormateada = remember(uiState.tasaActual) {
        if (uiState.tasaActual > 0) String.format(Locale.US, "%.5f", 1 / uiState.tasaActual) else "0.00000"
    }

    // Lista fija de tus pares de divisas populares para la sección inferior
    val paresPopulares = remember {
        listOf(
            Pair("USD", "PEN"),
            Pair("PEN", "USD"),
            Pair("EUR", "USD"),
            Pair("USD", "EUR")
        )
    }

    // Animación para el botón de swap
    var rotacionGrados by remember { mutableFloatStateOf(0f) }
    val rotacionAnimada by animateFloatAsState(
        targetValue = rotacionGrados,
        animationSpec = tween(durationMillis = 500),
        label = "Rotación Swap"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tipo de Cambio (T.C.)", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrownTheme
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ============================================================
            // 1. SECCIÓN DE LA CALCULADORA INTERACTIVA
            // ============================================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().background(color = LightBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),

                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Calculadora de Divisas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Selectores de Monedas (Tengo -> Quiero)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SelectorMonedaDesplegable(
                                label = "Tengo",
                                seleccionada = uiState.monedaTengo,
                                monedas = uiState.monedasDisponibles,
                                onMonedaSeleccionada = { viewModel.seleccionarMonedaTengo(it) },
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    rotacionGrados += 180f
                                    viewModel.invertirMonedas()
                                },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .rotate(rotacionAnimada)
                            ) {
                                Icon(
                                    Icons.Default.SwapHoriz,
                                    contentDescription = "Invertir monedas",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            SelectorMonedaDesplegable(
                                label = "Quiero",
                                seleccionada = uiState.monedaQuiero,
                                monedas = uiState.monedasDisponibles,
                                onMonedaSeleccionada = { viewModel.seleccionarMonedaQuiero(it) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Input para ingresar el monto
                        OutlinedTextField(
                            value = uiState.cantidad,
                            onValueChange = { viewModel.actualizarCantidad(it) },
                            label = { Text("Cantidad a cambiar") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Input de Resultado (Lectura)
                        OutlinedTextField(
                            value = cantidadConvertida,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Resultado") },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            trailingIcon = {
                                if (uiState.cargandoTasa) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tasas informativas unitarias inferiores
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "1 ${uiState.monedaTengo} = $tasaFormateada ${uiState.monedaQuiero}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "1 ${uiState.monedaQuiero} = $tasaInversaFormateada ${uiState.monedaTengo}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ============================================================
            // 2. SECCIÓN TITULO: TASAS DE CAMBIO
            // ============================================================
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Tasas de cambio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pares de divisas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ============================================================
            // 3. BUCLE DE PARES POPULARES (Diseño idéntico a tu captura)
            // ============================================================
            items(paresPopulares) { par ->
                val codigoOrigen = par.first
                val codigoDestino = par.second

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.aplicarConversionPopular(base = codigoOrigen, destino = codigoDestino)
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Autorenew,
                                contentDescription = null,
                                tint = Color(0xFF9C7C5D), // Color marrón estilizado
                                modifier = Modifier.size(26.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "$codigoOrigen → $codigoDestino",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "11/07 05:15",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        // Indicador visual de acción para el usuario
                        Text(
                            text = "Calcular",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .background(
                                    color = LightBackground,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

