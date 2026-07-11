package com.example.smallchangedam.presentation.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smallchangedam.data.RetrofitClient
import com.example.smallchangedam.data.TransaccionRequest
import com.example.smallchangedam.presentation.home.ColorMarron
import com.example.smallchangedam.presentation.home.ColorVerdeTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale
import com.example.smallchangedam.data.OfertaStatusUpdateRequest

enum class PaymentScreenState {
    FORMULARIO,
    PROCESANDO,
    EXITOSO
}

enum class PaymentMethod(val title: String, val icon: ImageVector) {
    YAPE_PLIN("Yape / Plin", Icons.Default.PhoneAndroid),
    CARD("Tarjeta Crédito/Débito", Icons.Default.CreditCard),
    DIGITAL_WALLET("Billetera Digital", Icons.Default.Wallet)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentGatewayMockupScreen(
    navController: NavController,
    ofertaId: Int = 1,
    vendedorId: Int = 4, // ID del usuario al que se va a calificar
    clienteLogueadoId: Int = 1, // ID del comprador actual (se asume 1 para el flujo)
    montoAComprar: Double = 150.00
) {
    val coroutineScope = rememberCoroutineScope()
    var currentScreenState by remember { mutableStateOf(PaymentScreenState.FORMULARIO) }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.YAPE_PLIN) }

    // Configuración regional observable para formatear monedas sin warnings
    val locale = LocalConfiguration.current.locales[0]
    val montoFormateado = remember(montoAComprar, locale) {
        String.format(locale, "%.2f", montoAComprar)
    }

    // Estados del Diálogo de Calificación
    var mostrarModalCalificacion by remember { mutableStateOf(false) }
    var notaCalificacion by remember { mutableDoubleStateOf(5.0) }
    var calificadoExitosamente by remember { mutableStateOf(false) }

    // Estados para los formularios
    var celular by remember { mutableStateOf("") }
    var codigoAprobacion by remember { mutableStateOf("") }
    var numeroTarjeta by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var nombreTitular by remember { mutableStateOf("") }
    var correoBilletera by remember { mutableStateOf("") }

    var transaccionIdGenerado by remember { mutableStateOf("TX-PENDIENTE") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pasarela de Pago", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorMarron),
                navigationIcon = {
                    if (currentScreenState == PaymentScreenState.FORMULARIO) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                        }
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
            when (currentScreenState) {
                PaymentScreenState.FORMULARIO -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Resumen de la Compra
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = ColorMarron.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Resumen de la oferta seleccionada", fontSize = 13.sp, color = Color.DarkGray)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Total a transferir:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "S/ $montoFormateado",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorMarron
                                    )
                                }
                            }
                        }

                        Text(text = "Selecciona tu método de pago:", fontSize = 15.sp, fontWeight = FontWeight.Medium)

                        // Selector de métodos de pago
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PaymentMethod.values().forEach { method ->
                                val isSelected = selectedMethod == method
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedMethod = method },
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) ColorMarron else Color.LightGray
                                    ),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = if (isSelected) ColorMarron.copy(alpha = 0.05f) else Color.Transparent
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = method.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) ColorMarron else Color.Gray
                                        )
                                        Text(
                                            text = method.title,
                                            modifier = Modifier.weight(1f),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedMethod = method },
                                            colors = RadioButtonDefaults.colors(selectedColor = ColorMarron)
                                        )
                                    }
                                }
                            }
                        }

                        // Formularios Dinámicos
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                when (selectedMethod) {
                                    PaymentMethod.YAPE_PLIN -> {
                                        Text("Pago inmediato con Yape o Plin", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ColorMarron)
                                        OutlinedTextField(
                                            value = celular,
                                            onValueChange = { if (it.length <= 9) celular = it },
                                            label = { Text("Número de celular") },
                                            modifier = Modifier.fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                                        )
                                        OutlinedTextField(
                                            value = codigoAprobacion,
                                            onValueChange = { if (it.length <= 6) codigoAprobacion = it },
                                            label = { Text("Código de aprobación (6 dígitos)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                    }
                                    PaymentMethod.CARD -> {
                                        Text("Tarjeta de Crédito / Débito bancaria", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ColorMarron)
                                        OutlinedTextField(
                                            value = nombreTitular,
                                            onValueChange = { nombreTitular = it },
                                            label = { Text("Nombre impreso en tarjeta") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = numeroTarjeta,
                                            onValueChange = { numeroTarjeta = it },
                                            label = { Text("Número de tarjeta") },
                                            modifier = Modifier.fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            OutlinedTextField(
                                                value = fechaVencimiento,
                                                onValueChange = { fechaVencimiento = it },
                                                label = { Text("MM/AA") },
                                                modifier = Modifier.weight(1f),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                            OutlinedTextField(
                                                value = cvv,
                                                onValueChange = { cvv = it },
                                                label = { Text("CVV") },
                                                modifier = Modifier.weight(1f),
                                                visualTransformation = PasswordVisualTransformation(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        }
                                    }
                                    PaymentMethod.DIGITAL_WALLET -> {
                                        Text("Cuentas Digitales Alternativas", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ColorMarron)
                                        OutlinedTextField(
                                            value = correoBilletera,
                                            onValueChange = { correoBilletera = it },
                                            label = { Text("Correo electrónico de la cuenta") },
                                            modifier = Modifier.fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón Procesar Pago
                        Button(
                            onClick = {
                                currentScreenState = PaymentScreenState.PROCESANDO
                                coroutineScope.launch {
                                    try {
                                        delay(2500) // Simulación de carga estética tipo Rappi

                                        val status = OfertaStatusUpdateRequest(estado = false)
                                        // 1. LLAMADA API I: Cambiar estado de oferta a false (Inactiva) usando el PATCH definido
                                        RetrofitClient.apiService.actualizarEstadoOferta(id = ofertaId, request = status)

                                        // 2. LLAMADA API II: Registrar la transacción mediante POST pasando el DTO exacto
                                        val txRequest = TransaccionRequest(
                                            ofertaId = ofertaId,
                                            clienteCompradorId = clienteLogueadoId
                                        )

                                        // Enviamos la request al backend junto con el ID del usuario por QueryParam
                                        val apiResponse = RetrofitClient.apiService.crearTransaccion(
                                            request = txRequest,
                                            usuarioId = clienteLogueadoId
                                        )
                                        transaccionIdGenerado = "TX-${apiResponse.id}"

                                    } catch (e: Exception) {
                                        // Fallback en caso de error de conexión para no romper el flujo visual en pruebas
                                        transaccionIdGenerado = "TX-${(100000..999999).random()}"
                                    } finally {
                                        currentScreenState = PaymentScreenState.EXITOSO
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorMarron)
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Procesar Pago Seguro", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                PaymentScreenState.PROCESANDO -> {
                    VistaProcesandoPago()
                }

                PaymentScreenState.EXITOSO -> {
                    VistaCompraExitosa(
                        monto = montoAComprar,
                        transaccionId = transaccionIdGenerado,
                        metodoPago = selectedMethod.title,
                        locale = locale,
                        onCalificarClick = { mostrarModalCalificacion = true },
                        onVolverInicioClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
                    )
                }
            }
        }
    }

    // Modal/Popup para calificar la oferta (vendedor)
    if (mostrarModalCalificacion) {
        AlertDialog(
            onDismissRequest = { mostrarModalCalificacion = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                // LLAMADA API III: Calificar usuario calculando el promedio matemático en C#
                                RetrofitClient.apiService.calificarUsuario(id = vendedorId, calificacion = notaCalificacion)
                                calificadoExitosamente = true
                                delay(1200)
                            } catch (e: Exception) {
                                calificadoExitosamente = true
                            } finally {
                                mostrarModalCalificacion = false
                            }
                        }
                    },
                    enabled = !calificadoExitosamente
                ) {
                    Text(if (calificadoExitosamente) "Enviado ✓" else "Enviar Calificación", color = ColorMarron, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarModalCalificacion = false }) {
                    Text("Omitir", color = Color.Gray)
                }
            },
            title = { Text("Califica al Vendedor", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "¿Cómo calificarías tu experiencia de intercambio con este usuario?",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..5) {
                            val estrellaInvolucrada = i.toDouble()
                            val esActiva = estrellaInvolucrada <= notaCalificacion
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Estrella $i",
                                tint = if (esActiva) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { notaCalificacion = estrellaInvolucrada }
                            )
                        }
                    }
                    Text(
                        text = "Puntaje seleccionado: $notaCalificacion / 5.0",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = ColorMarron
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun VistaProcesandoPago() {
    val infiniteTransition = rememberInfiniteTransition(label = "Carga")
    val anguloRotacion by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "Rotar"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .background(ColorMarron.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = ColorMarron,
                modifier = Modifier
                    .size(50.dp)
                    .rotate(anguloRotacion)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Procesando pago seguro...",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = ColorMarron
        )
        Text(
            text = "Estamos validando tu transacción bancaria con el servidor. No cierres la aplicación.",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun VistaCompraExitosa(
    monto: Double,
    transaccionId: String,
    metodoPago: String,
    locale: Locale,
    onCalificarClick: () -> Unit,
    onVolverInicioClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val montoFinalFormateado = remember(monto, locale) { String.format(locale, "%.2f", monto) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Éxito",
                tint = ColorVerdeTag,
                modifier = Modifier.size(72.dp)
            )

            Text("¡Pago Procesado con Éxito!", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = ColorVerdeTag)
            Text("La operación fue registrada bajo transacciones seguras de Small Change.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("DETALLE DE LA TRANSACCIÓN", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = ColorMarron)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    FilaComprobante(label = "ID Operación:", valor = transaccionId)
                    FilaComprobante(label = "Fecha y Hora:", valor = SimpleDateFormat("dd/MM/yyyy HH:mm", LocalLocale.current.platformLocale).format(Date()))
                    FilaComprobante(label = "Medio de Pago:", valor = metodoPago)

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Debitado:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("S/ $montoFinalFormateado", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ColorMarron)
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Comprobante guardado en la carpeta de Descargas")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorMarron),
                border = BorderStroke(1.dp, ColorMarron)
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Descargar Comprobante PDF")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onCalificarClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calificar al Vendedor", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onVolverInicioClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorMarron),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Regresar al Inicio", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
fun FilaComprobante(label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.End
        )
    }
}