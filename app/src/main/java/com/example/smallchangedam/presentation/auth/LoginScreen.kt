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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.google.gson.Gson

import com.example.smallchangedam.presentation.components.CustomInputField
import com.example.smallchangedam.data.LoginRequest
import com.example.smallchangedam.data.RetrofitClient
import com.example.smallchangedam.data.SessionManager
import com.example.smallchangedam.data.ValidationErrorResponse

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

    // Conexión API
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
                    text = "Ingrese su correo:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                CustomInputField(
                    value = usuario,
                    onValueChange = {
                        usuario = it
                        errorText = null
                    },
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
                    onValueChange = {
                        contrasena = it
                        errorText = null
                    },
                    placeholder = "● ● ● ● ● ● ● ●",
                    isPassword = true
                )
                Text(
                    text = "¿Olvidaste tu contraseña?",
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
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (usuario.isBlank() || contrasena.isBlank()) {
                        errorText = "Por favor, completa todos los campos"
                        return@Button
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(usuario).matches()) {
                        errorText = "Ingresa un correo electrónico válido"
                        return@Button
                    }

                    // Conexión al Backend
                    coroutineScope.launch {
                        isLoading = true
                        errorText = null

                        try {
                            val request = LoginRequest(
                                email = usuario,
                                password = contrasena
                            )

                            val response = RetrofitClient.apiService.loginUsuario(request)
                            SessionManager.authToken = response.token
                            onLoginSuccess()

                        } catch (e: HttpException) {
                            // Procesamiento de errores HTTP
                            when (e.code()) {
                                400 -> {
                                    val errorBody = e.response()?.errorBody()?.string()
                                    if (!errorBody.isNullOrEmpty()) {
                                        try {
                                            val errorResponse = Gson().fromJson(errorBody, ValidationErrorResponse::class.java)

                                            val primerError = errorResponse.errors?.values?.firstOrNull()?.firstOrNull()
                                            errorText = primerError ?: "Error en los datos ingresados"
                                        } catch (parseEx: Exception) {
                                            errorText = "Error al procesar la validación."
                                        }
                                    } else {
                                        errorText = "Datos ingresados no válidos."
                                    }
                                }
                                401 -> {
                                    errorText = "Correo o contraseña incorrectos."
                                }
                                else -> {
                                    errorText = "Error del servidor: ${e.code()}"
                                }
                            }
                        } catch (e: Exception) {
                            errorText = "Error de red: revisa tu conexión a internet."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
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