package com.example.smallchangedam.presentation.transactions

import android.R.attr.horizontalDivider
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Enum para manejar los métodos de pago de forma limpia
enum class PaymentMethod(val title: String, val icon: ImageVector) {
    YAPE_PLIN("Yape / Plin", Icons.Default.PhoneAndroid),
    CARD("Tarjeta Crédito/Débito", Icons.Default.CreditCard),
    DIGITAL_WALLET("Billetera Digital", Icons.Default.Wallet)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentGatewayMockupScreen(
    navController: NavController,
    montoAComprar: Double = 150.00,
    onPaymentSuccess: () -> Unit = {}
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.YAPE_PLIN) }

    // Estados para los formularios
    var celular by remember { mutableStateOf("") }
    var codigoAprobacion by remember { mutableStateOf("") }

    var numeroTarjeta by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var nombreTitular by remember { mutableStateOf("") }

    var correoBilletera by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pasarela de Pago", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumen de la Compra
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Resumen del pedido", fontSize = 14.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total a pagar:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "S/ ${String.format(java.util.Locale.US, "%.2f", montoAComprar.toDouble())}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Text(text = "Selecciona tu método de pago:", fontSize = 16.sp, fontWeight = FontWeight.Medium)

            // Selector de métodos de pago (Botones tipo fila)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentMethod.values().forEach { method ->
                    val isSelected = selectedMethod == method
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = method },
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
                        ),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = method.icon,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                            Text(
                                text = method.title,
                                modifier = Modifier.weight(1f),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedMethod = method }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Formularios Dinámicos según la selección
            Box(modifier = Modifier.weight(1f)) {
                when (selectedMethod) {
                    PaymentMethod.YAPE_PLIN -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Pago con Yape o Plin", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Escanea nuestro QR o ingresa tu número de celular registrado junto a tu código de aprobación.", fontSize = 13.sp, color = Color.Gray)

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
                    }
                    PaymentMethod.CARD -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Tarjeta de Crédito o Débito", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                            OutlinedTextField(
                                value = nombreTitular,
                                onValueChange = { nombreTitular = it },
                                label = { Text("Nombre del titular") },
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
                    }
                    PaymentMethod.DIGITAL_WALLET -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Otras Billeteras Digitales", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Inicia sesión en tu proveedor de billetera digital global (PayPal, PayU, etc.) para procesar el pago.", fontSize = 13.sp, color = Color.Gray)

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

            // Botón de Acción de Pago
            Button(
                onClick = { onPaymentSuccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Procesar Pago Seguro", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}