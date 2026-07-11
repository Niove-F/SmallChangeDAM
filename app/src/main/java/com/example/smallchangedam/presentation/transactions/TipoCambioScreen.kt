package com.example.smallchangedam.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smallchangedam.data.RetrofitClient
import com.example.smallchangedam.presentation.home.ColorMarron
import com.example.smallchangedam.presentation.home.ColorVerdeTag
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

data class ParDivisa(
    val origen: String,
    val destino: String,
    val tasa: Double,
    val actualizacion: String
)

val paresTipoCambio = listOf(
    "USD" to "PEN",
    "PEN" to "USD",
    "EUR" to "USD",
    "USD" to "EUR"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoCambioScreen(navController: NavController) {
    var tasas by remember { mutableStateOf<List<ParDivisa>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var totalOk by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null

        try {
            val monedas = RetrofitClient.apiService.obtenerMonedas()
            if (monedas.isEmpty()) {
                errorMessage = "No hay divisas disponibles en el servidor."
                isLoading = false
                return@LaunchedEffect
            }
        } catch (e: Exception) {
            errorMessage = "Error de conexión al servidor: ${e.message}"
            isLoading = false
            return@LaunchedEffect
        }

        val resultados = mutableListOf<ParDivisa>()
        var ok = 0

        for ((origen, destino) in paresTipoCambio) {
            try {
                delay(150)
                val resp = RetrofitClient.apiService.obtenerTipoCambio(origen, destino)
                if (resp.tipoCambio > 0) {
                    resultados.add(ParDivisa(resp.monedaIn, resp.monedaOut, resp.tipoCambio, resp.fechaActualizacion))
                    ok++
                } else {
                    resultados.add(ParDivisa(origen, destino, -1.0, "Valor no disponible"))
                }
            } catch (e: Exception) {
                val msg = e.message ?: "Error"
                resultados.add(ParDivisa(origen, destino, -1.0, msg.take(30)))
            }
        }

        tasas = resultados
        totalOk = ok
        if (ok == 0) {
            val primerError = resultados.firstOrNull()?.actualizacion ?: ""
            errorMessage = "No se pudieron obtener las tasas ($primerError)"
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tipo de Cambio (T.C.)", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorMarron),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ColorMarron)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Cargando tasas...", color = Color.Gray)
                        }
                    }
                }
                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                            Icon(Icons.Default.CurrencyExchange, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(errorMessage!!, color = Color.Red, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorMarron)
                            ) { Text("Regresar") }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "Tasas de cambio",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorMarron
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val estado = if (totalOk < tasas.size) "($totalOk de ${tasas.size} disponibles)" else ""
                            Text(
                                "Pares de divisas $estado",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                        items(tasas) { par ->
                            val disponible = par.tasa > 0
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.CurrencyExchange,
                                            contentDescription = null,
                                            tint = if (disponible) ColorMarron else Color.Gray,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                "${par.origen} → ${par.destino}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            if (disponible) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Update, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(formatFechaTipoCambio(par.actualizacion), fontSize = 11.sp, color = Color.Gray)
                                                }
                                            } else {
                                                Text(par.actualizacion, fontSize = 11.sp, color = Color.Red.copy(alpha = 0.7f))
                                            }
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (disponible) ColorVerdeTag.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = if (disponible) String.format("%.4f", par.tasa) else "---",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            color = if (disponible) ColorVerdeTag else Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Deprecated(
    message = "Usa TipoCambioScreen(navController)",
    replaceWith = ReplaceWith("TipoCambioScreen(navController)")
)
@Composable
fun TasaCambioScreen(navController: NavController) {
    TipoCambioScreen(navController = navController)
}

private fun formatFechaTipoCambio(fecha: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(fecha)
        val outputFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        outputFormat.format(date!!)
    } catch (_: Exception) {
        fecha.take(10)
    }
}
