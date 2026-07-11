package com.example.smallchangedam.presentation.offers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Visibility
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
import com.example.smallchangedam.data.OfertaResponse
import com.example.smallchangedam.data.RetrofitClient
import com.example.smallchangedam.data.SessionManager
import com.example.smallchangedam.presentation.home.ColorMarron
import com.example.smallchangedam.presentation.home.ColorVerdeTag
import com.example.smallchangedam.presentation.home.calcularTiempoTranscurrido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisOfertasScreen(navController: NavController) {
    val context = LocalContext.current
    var ofertas by remember { mutableStateOf<List<OfertaResponse>>(emptyList()) }
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

            val todas = RetrofitClient.apiService.listarOfertas()
            ofertas = todas.filter { it.clienteId == userId }.sortedByDescending { it.fechaCreacion }
        } catch (e: Exception) {
            errorMessage = "Error al cargar tus ofertas: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Ofertas", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorMarron),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("publicarOferta") },
                containerColor = ColorMarron,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva oferta")
            }
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
                ofertas.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.RemoveRedEye, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No tienes ofertas publicadas", fontSize = 16.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Toca + para crear una nueva", fontSize = 13.sp, color = Color.LightGray)
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
                                "Tienes ${ofertas.size} oferta${if (ofertas.size != 1) "s" else ""} publicada${if (ofertas.size != 1) "s" else ""}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        items(ofertas, key = { it.id }) { oferta ->
                            OfertaPropiaCard(oferta = oferta, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfertaPropiaCard(oferta: OfertaResponse, navController: NavController) {
    val montoRecibido = oferta.cantidad * oferta.tipoCambio
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("detallesOferta/${oferta.id}") },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${oferta.monedaAEnviar} → ${oferta.monedaARecibir}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ColorMarron
                )
                if (oferta.estado == true) {
                    Surface(
                        color = ColorVerdeTag.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ColorVerdeTag, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Activa", color = ColorVerdeTag, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Surface(
                        color = Color.Gray.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Inactiva",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ofreces:", fontSize = 13.sp, color = Color.Gray)
                Text("${String.format("%.2f", oferta.cantidad)} ${oferta.monedaAEnviar}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Recibes:", fontSize = 13.sp, color = Color.Gray)
                Text("${String.format("%.2f", montoRecibido)} ${oferta.monedaARecibir}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(ColorMarron.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "TC: ${String.format("%.4f", oferta.tipoCambio)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ColorMarron
                    )
                }
                Text(
                    calcularTiempoTranscurrido(oferta.fechaCreacion),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
