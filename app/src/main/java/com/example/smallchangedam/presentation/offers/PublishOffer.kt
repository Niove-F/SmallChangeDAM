package com.example.smallchangedam.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.smallchangedam.data.CambioMonedaRequest
import com.example.smallchangedam.data.OfertaRequest
import com.example.smallchangedam.data.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlinx.coroutines.launch

private val ColorVerdeTag = Color(0xFF72C075)
val BrownTheme = Color(0xFFA67C52)
val LightBackground = Color(0xFFFAFAFA)
val AlertBackground = Color(0xFFF7F2F2)
val AlertRed = Color(0xFFB71C1C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishOfferScreen(
    onNavigateBack: () -> Unit,
    onOfferPublished: () -> Unit
) {
    var tengoCurrency by remember { mutableStateOf("") }
    var quieroCurrency by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("1000.00") }
    var tasaCambio by remember { mutableStateOf("1.17") }
    var montoConvertido by remember { mutableStateOf<Double?>(null) }
    var ultimaActualizacion by remember { mutableStateOf<String?>(null) }
    var currencyOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isPublishing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var expandedTengo by remember { mutableStateOf(false) }
    var expandedQuiero by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Carga inicial del catálogo de divisas
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        try {
            val responseBody = RetrofitClient.apiService.obtenerMonedas()
            val jsonString = responseBody.string()
            Log.d("API_DIVISAS", "Respuesta exacta recibida: $jsonString")
            if (jsonString.trim().isNotEmpty()) {
                val gson = Gson()
                val listaMonedas: List<String> = if (jsonString.trim().startsWith("{")) {
                    val tipoMapa = object : TypeToken<Map<String, String>>() {}.type
                    val mapa: Map<String, String> = gson.fromJson(jsonString, tipoMapa)
                    mapa.keys.toList()
                } else {
                    val tipoLista = object : TypeToken<List<String>>() {}.type
                    gson.fromJson(jsonString, tipoLista)
                }
                if (listaMonedas.isNotEmpty()) {
                    val listaOrdenada = listaMonedas.sorted()
                    currencyOptions = listaOrdenada
                    if (listaOrdenada.contains("USD")) tengoCurrency = "USD"
                    if (listaOrdenada.contains("EUR")) quieroCurrency = "EUR"
                    if (tengoCurrency.isEmpty()) tengoCurrency = listaOrdenada.first()
                    if (quieroCurrency.isEmpty() && listaOrdenada.size > 1) quieroCurrency = listaOrdenada[1]
                } else {
                    errorMessage = "La API no retornó ninguna divisa disponible."
                }
            } else {
                errorMessage = "El servidor envió una respuesta de catálogo vacía."
            }
        } catch (e: Exception) {
            errorMessage = "Error al cargar las divisas de la API: ${e.message}"
            Log.e("API_DIVISAS", "Fallo al procesar las monedas", e)
        } finally {
            isLoading = false
        }
    }

    // Actualización de tasas de cambio según selección
    LaunchedEffect(tengoCurrency, quieroCurrency) {
        if (tengoCurrency.isBlank() || quieroCurrency.isBlank() || tengoCurrency == quieroCurrency) return@LaunchedEffect
        try {
            val tipoCambioResp = RetrofitClient.apiService.obtenerTipoCambio(
                monedaOrigen = tengoCurrency,
                monedaDestino = quieroCurrency
            )
            tasaCambio = tipoCambioResp.tipoCambio.toString()
            ultimaActualizacion = tipoCambioResp.fechaActualizacion
            val cantDouble = cantidad.toDoubleOrNull() ?: 0.0
            if (cantDouble > 0.0) {
                val cambioResp = RetrofitClient.apiService.convertirMoneda(
                    CambioMonedaRequest(
                        monedaIn = tengoCurrency,
                        monedaOut = quieroCurrency,
                        monto = cantDouble
                    )
                )
                montoConvertido = cambioResp.montoConvertido
            } else {
                montoConvertido = null
            }
        } catch (e: Exception) {
            Log.e("API_DIVISAS", "Error al obtener tipo de cambio o conversión: ${e.message}", e)
            errorMessage = "No fue posible obtener la tasa de cambio: ${e.message}"
        }
    }

    // Cálculo dinámico local cuando cambia cantidad o tasa
    LaunchedEffect(cantidad, tasaCambio) {
        val cantDouble = cantidad.toDoubleOrNull()
        val tasaDouble = tasaCambio.toDoubleOrNull()
        if (cantDouble != null && tasaDouble != null) {
            montoConvertido = cantDouble * tasaDouble
        }
    }

    fun publicarOferta() {
        val cantDouble = cantidad.toDoubleOrNull()
        val tasaDouble = tasaCambio.toDoubleOrNull()
        if (cantDouble == null || cantDouble <= 0.0) {
            errorMessage = "La cantidad a intercambiar debe ser mayor a cero."
            return
        }
        if (tasaDouble == null || tasaDouble <= 0.0) {
            errorMessage = "La tasa de cambio debe ser mayor a cero."
            return
        }
        if (tengoCurrency == quieroCurrency) {
            errorMessage = "La moneda de origen y destino no pueden ser iguales."
            return
        }
        scope.launch {
            isPublishing = true
            errorMessage = null
            try {
                val request = OfertaRequest(
                    monedaAEnviar = tengoCurrency,
                    monedaARecibir = quieroCurrency,
                    cantidad = cantDouble,
                    tipoCambio = tasaDouble
                )
                RetrofitClient.apiService.crearOferta(request)
                successMessage = "¡Oferta publicada con éxito!"
                onOfferPublished()
            } catch (e: Exception) {
                errorMessage = "Error al publicar la oferta: ${e.message}"
                Log.e("PUBLISH_OFFER", "Error en POST api/ofertas", e)
            } finally {
                isPublishing = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Publicar nueva oferta",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrownTheme)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Detalles de la Oferta",
                        style = MaterialTheme.typography.titleLarge,
                        color = BrownTheme,
                        fontWeight = FontWeight.Bold
                    )

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = BrownTheme)
                        }
                    }

                    // --- Dropdown Tengo ---
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = expandedTengo,
                            onExpandedChange = { if (currencyOptions.isNotEmpty()) expandedTengo = !expandedTengo }
                        ) {
                            OutlinedTextField(
                                value = if (isLoading) "Cargando divisas..." else tengoCurrency,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tengo") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTengo) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = BrownTheme,
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    cursorColor = BrownTheme
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                                    .fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = expandedTengo,
                                onDismissRequest = { expandedTengo = false }
                            ) {
                                currencyOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            tengoCurrency = option
                                            expandedTengo = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // --- Dropdown Quiero ---
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = expandedQuiero,
                            onExpandedChange = { if (currencyOptions.isNotEmpty()) expandedQuiero = !expandedQuiero }
                        ) {
                            OutlinedTextField(
                                value = if (isLoading) "Cargando divisas..." else quieroCurrency,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Quiero") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedQuiero) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = BrownTheme,
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    cursorColor = BrownTheme
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                                    .fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = expandedQuiero,
                                onDismissRequest = { expandedQuiero = false }
                            ) {
                                currencyOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            quieroCurrency = option
                                            expandedQuiero = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // --- Input Cantidad ---
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text("Cantidad a intercambiar") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = BrownTheme,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            cursorColor = BrownTheme
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // --- Input Tasa Cambio ---
                    OutlinedTextField(
                        value = tasaCambio,
                        onValueChange = { tasaCambio = it },
                        label = { Text("Tasa de cambio") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = BrownTheme,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            cursorColor = BrownTheme
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // --- Vista previa conversión ---
                    montoConvertido?.let { convertido ->
                        val formato = DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US))
                        Surface(
                            color = AlertBackground,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Monto convertido (${quieroCurrency}): ${formato.format(convertido)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = BrownTheme,
                                    fontWeight = FontWeight.SemiBold
                                )
                                ultimaActualizacion?.let { fecha ->
                                    Text(
                                        text = "Última actualización: $fecha",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }

                    // --- Mensajes de error/éxito ---
                    errorMessage?.let {
                        Text(text = it, color = AlertRed, modifier = Modifier.padding(top = 4.dp))
                    }

                    successMessage?.let {
                        Surface(
                            color = ColorVerdeTag.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = it,
                                color = ColorVerdeTag,
                                modifier = Modifier.padding(12.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- Botonera inferior ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = { publicarOferta() },
                            enabled = !isLoading && !isPublishing && currencyOptions.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = BrownTheme),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isPublishing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Text("Publicar Oferta", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}