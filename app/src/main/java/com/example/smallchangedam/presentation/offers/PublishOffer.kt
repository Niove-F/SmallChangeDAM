package com.example.smallchangedam.presentation.offers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// Importa tus clases de datos y tu cliente Retrofit (Asegúrate de que estas rutas coincidan con tu proyecto)
import com.example.smallchangedam.data.OfertaRequest
import com.example.smallchangedam.data.RetrofitClient

val BrownTheme = Color(0xFFA67C52)
val LightBackground = Color(0xFFFAFAFA)
val AlertBackground = Color(0xFFF7F2F2)
val AlertRed = Color(0xFFB71C1C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishOfferScreen(navController: NavController) {
    var tengoExpanded by remember { mutableStateOf(false) }
    var quieroExpanded by remember { mutableStateOf(false) }
    var tengoCurrency by remember { mutableStateOf("USD - Dolares") }
    var quieroCurrency by remember { mutableStateOf("EUR - Euro") }
    var cantidad by remember { mutableStateOf("1000.00") }
    var tasa by remember { mutableStateOf("1.17") }

    // CONEXIÓN API ---
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // Scope para lanzar la petición de red en segundo plano
    val coroutineScope = rememberCoroutineScope()

    val currencyOptions = listOf("USD - Dolares", "EUR - Euro", "PEN - Soles")
    val tasaValue = tasa.toDoubleOrNull() ?: 0.0
    val isRateLow = tasaValue < 1.5
    // El botón solo se habilita si los datos son válidos y NO está cargando
    val isButtonEnabled = !isRateLow && tasa.isNotEmpty() && cantidad.isNotEmpty() && !isLoading

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrownTheme)
                .padding(start = 16.dp, top = 40.dp, bottom = 10.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Publicar nueva oferta",
                color = Color.White,
                fontSize = 20.sp,
            )
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Volver | Inicio > Mis ofertas > Publicar...",
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 16.dp).clickable { navController.popBackStack() }
            )

            Text(
                text = "Detalles de la Oferta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text("Tengo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = tengoExpanded,
                onExpandedChange = { tengoExpanded = !tengoExpanded }
            ) {
                OutlinedTextField(
                    value = tengoCurrency,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tengoExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                ExposedDropdownMenu(
                    expanded = tengoExpanded,
                    onDismissRequest = { tengoExpanded = false }
                ) {
                    currencyOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                tengoCurrency = selectionOption
                                tengoExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Quiero", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = quieroExpanded,
                onExpandedChange = { quieroExpanded = !quieroExpanded }
            ) {
                OutlinedTextField(
                    value = quieroCurrency,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = quieroExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                ExposedDropdownMenu(
                    expanded = quieroExpanded,
                    onDismissRequest = { quieroExpanded = false }
                ) {
                    currencyOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                quieroCurrency = selectionOption
                                quieroExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Cantidad a intercambiar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = { Text(tengoCurrency.split(" ")[0], color = Color.DarkGray, modifier = Modifier.padding(end = 16.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Tasa de cambio", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = tasa,
                onValueChange = { tasa = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = { Text("${quieroCurrency.split(" ")[0]}/${tengoCurrency.split(" ")[0]}", color = Color.DarkGray, modifier = Modifier.padding(end = 16.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
            )

            if (isRateLow && tasa.isNotEmpty()) {
                Text(
                    text = "Esta tasa es muy baja",
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }

            // MOSTRAR MENSAJE DE ERROR DE LA API ---
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { navController.popBackStack() },
                    enabled = !isLoading // Deshabilitar si está cargando
                ) {
                    Text("Cancelar", color = if(isLoading) Color.LightGray else Color.DarkGray)
                }

                Button(
                    // Conexion API
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null

                            try {
                                val monedaOrigen = tengoCurrency.split(" ")[0]
                                val monedaDestino = quieroCurrency.split(" ")[0]

                                val request = OfertaRequest(
                                    monedaAEnviar = monedaOrigen,
                                    monedaARecibir = monedaDestino,
                                    cantidad = cantidad.toDoubleOrNull() ?: 0.0,
                                    tipoCambio = tasa.toDoubleOrNull() ?: 0.0
                                )

                                // Llamada HTTP
                                val response = RetrofitClient.apiService.crearOferta(request)

                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true } // Limpiar historial
                                }

                            } catch (e: Exception) {
                                errorMessage = "Error de conexión: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = isButtonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrownTheme,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                        .height(48.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Publicar Oferta", color = if (isButtonEnabled) Color.White else Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AlertBackground)
                    .border(width = 0.dp, color = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(64.dp)
                        .background(AlertRed)
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Existen (1) ofertas similares a la tuya.",
                        color = AlertRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Presiona aquí para verlas",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { /*navController.navigate("similar")*/}
                    )
                }
            }
        }
    }
}