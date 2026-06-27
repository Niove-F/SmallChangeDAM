package com.example.smallchangedam.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Importamos tus clases de datos, cliente API y componentes
import com.example.smallchangedam.presentation.components.CustomInputField
import com.example.smallchangedam.data.LoginRequest
import com.example.smallchangedam.data.RetrofitClient
import com.example.smallchangedam.data.SessionManager

private val ColorMarron = Color(0xFFB08968)
private val ColorGrisClaro = Color(0xFFD9D9D9)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    // CONEXIÓN API ---
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val curveShape = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height * 0.75f)
        quadraticTo(
            size.width / 2f, size.height,
            0f, size.height * 0.75f
        )
        close()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header con curva
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(ColorGrisClaro, shape = curveShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 10.dp, top = 15.dp)
            ) {
                Text(
                    text = "Bienvenido de vuelta",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 40.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.width(220.dp),
                    thickness = 0.5.dp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¿No estás registrado?",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "Crea una Cuenta",
                    fontSize = 14.sp,
                    color = Color(0xFF3F51B5),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { onNavigateToRegister() }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Formulario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = "Ingrese su correo:", // Cambiado a correo para mayor precisión
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                CustomInputField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    placeholder = "Ej.: pepitopancracio@yahoo.com"
                )
            }

            Column {
                Text(
                    text = "Ingrese su contraseña:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                CustomInputField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    placeholder = "● ● ● ● ● ● ● ●",
                    isPassword = true
                )
                Text(
                    text = "Olvidaste tu contraseña?",
                    fontSize = 14.sp,
                    color = Color(0xFF3F51B5),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 4.dp)
                        .clickable { /* Acción recuperar contraseña */ }
                )
            }

            if (errorText != null) {
                Text(
                    text = errorText!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                // --- LÓGICA DE CONEXIÓN AL BACKEND AQUÍ ---
                onClick = {
                    if (usuario.isBlank() || contrasena.isBlank()) {
                        errorText = "Por favor, completa todos los campos"
                    } else {
                        coroutineScope.launch {
                            isLoading = true
                            errorText = null

                            try {
                                val request = LoginRequest(
                                    email = usuario,
                                    password = contrasena
                                )

                                // Llamada a tu API en Visual Studio
                                val response = RetrofitClient.apiService.loginUsuario(request)

                                // Guardamos el token en la memoria global (SessionManager)
                                SessionManager.authToken = response.token

                                // Si el login fue exitoso, navegamos al Home
                                onLoginSuccess()

                            } catch (e: Exception) {
                                // Capturamos errores como 401 (No autorizado) o falla de red
                                errorText = if (e.message?.contains("401") == true) {
                                    "Correo o contraseña incorrectos"
                                } else {
                                    "Error de conexión: ${e.message}"
                                }
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = !isLoading, // Se deshabilita para evitar múltiples clics
                modifier = Modifier
                    .width(240.dp)
                    .height(56.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorMarron,
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "INICIAR SESIÓN",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {})
}