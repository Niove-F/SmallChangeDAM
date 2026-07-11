package com.example.smallchangedam.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.content.Context
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smallchangedam.data.RetrofitClient
import com.example.smallchangedam.data.SessionManager
import com.example.smallchangedam.data.TransaccionResponse
import com.example.smallchangedam.presentation.home.ColorMarron
import com.example.smallchangedam.presentation.home.ColorVerdeTag
import com.example.smallchangedam.presentation.home.calcularTiempoTranscurrido
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialTransaccionesScreen(navController: NavController) {
    val context = LocalContext.current
    var transacciones by remember { mutableStateOf<List<TransaccionResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        try {
            val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            var userId = SessionManager.userId ?: prefs.getInt("user_id", 0)

            if (SessionManager.userId == null && userId > 0) {
                SessionManager.userId = userId
            }

            transacciones = RetrofitClient.apiService.listarTransacciones(userId)
                .sortedByDescending { it.fechaCreacion }
        } catch (e: Exception) {
            errorMessage = "Error al cargar el historial: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Transacciones", fontWeight = FontWeight.Bold, color = Color.White) },
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
                        CircularProgressIndicator(color = ColorMarron)
                    }
                }
                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(errorMessage!!, color = Color.Red)
                    }
                }
                transacciones.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No hay transacciones aún",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Cuando realices un intercambio,\naparecerá aquí.",
                                fontSize = 13.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
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
                                "${transacciones.size} transaccione${if (transacciones.size != 1) "s" else ""} registrada${if (transacciones.size != 1) "s" else ""}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        items(transacciones, key = { it.id }) { tx ->
                            TransaccionCard(tx)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransaccionCard(tx: TransaccionResponse) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (tx.estado.lowercase()) {
                    "completado", "completada", "exitosa", "aprobada" -> Icons.Default.CheckCircle
                    "cancelado", "cancelada", "rechazada" -> Icons.Default.Cancel
                    else -> Icons.Default.HourglassEmpty
                },
                contentDescription = null,
                tint = when (tx.estado.lowercase()) {
                    "completado", "completada", "exitosa", "aprobada" -> ColorVerdeTag
                    "cancelado", "cancelada", "rechazada" -> Color.Red
                    else -> Color(0xFFFFA000)
                },
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "TX-${tx.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    "Oferta #${tx.ofertaId}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    formatFechaTransaccion(tx.fechaCreacion),
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = when (tx.estado.lowercase()) {
                        "completado", "completada", "exitosa", "aprobada" -> ColorVerdeTag.copy(alpha = 0.15f)
                        "cancelado", "cancelada", "rechazada" -> Color.Red.copy(alpha = 0.15f)
                        else -> Color(0xFFFFA000).copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        tx.estado.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (tx.estado.lowercase()) {
                            "completado", "completada", "exitosa", "aprobada" -> ColorVerdeTag
                            "cancelado", "cancelada", "rechazada" -> Color.Red
                            else -> Color(0xFFFFA000)
                        }
                    )
                }
            }
        }
    }
}

private fun formatFechaTransaccion(fecha: String): String {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse(fecha)
        val output = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        output.format(date!!)
    } catch (_: Exception) {
        calcularTiempoTranscurrido(fecha)
    }
}
