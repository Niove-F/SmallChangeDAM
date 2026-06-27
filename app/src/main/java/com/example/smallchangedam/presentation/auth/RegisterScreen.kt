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
import com.example.smallchangedam.data.RegistroRequest
import com.example.smallchangedam.data.RetrofitClient
import com.example.smallchangedam.data.ValidationErrorResponse
import com.example.smallchangedam.presentation.components.CustomInputField
import com.google.gson.Gson
import kotlinx.coroutines.launch

private val ColorMarron = Color(0xFFB08968)
private val ColorGrisClaro = Color(0xFFD9D9D9)

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    var errorGeneral by remember { mutableStateOf<String?>(null) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorContrasena by remember { mutableStateOf<String?>(null) }

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
                    text = "Crear Cuenta",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.width(220.dp),
                    thickness = 0.5.dp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¿Ya tienes una cuenta?",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "Inicia Sesión",
                    fontSize = 14.sp,
                    color = Color(0xFF3F51B5),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { onNavigateToLogin() }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Formulario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            //usuario
            Column {
                CustomInputField(value = usuario, onValueChange = { usuario = it }, placeholder = "Ingrese su Usuario")
                Text(
                    text = "Su usuario será visible en todos lados.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                )
            }

            //correo
            Column {
                CustomInputField(value = correo, onValueChange = { correo = it; errorCorreo = null }, placeholder = "Correo Electrónico")
                if (errorCorreo != null) {
                    Text(
                        text = errorCorreo!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                    )
                }
            }

            //contraseña
            Column {
                CustomInputField(value = contrasena, onValueChange = { contrasena = it; errorContrasena = null }, placeholder = "Ingresar Contraseña", isPassword = true)
                if (errorContrasena != null) {
                    Text(
                        text = errorContrasena!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                    )
                }
            }

            CustomInputField(value = confirmarContrasena, onValueChange = { confirmarContrasena = it }, placeholder = "Confirmar Contraseña", isPassword = true)

            if (errorGeneral != null) {
                Text(
                    text = errorGeneral!!,
                    color = Color.Red,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = ColorMarron)
            } else {
                Button(
                    onClick = {
                        // 1. Validaciones previas locales elementales
                        when {
                            usuario.isBlank() || correo.isBlank() || contrasena.isBlank() -> {
                                errorGeneral = "Todos los campos son obligatorios"
                            }
                            contrasena != confirmarContrasena -> {
                                errorGeneral = "Las contraseñas no coinciden"
                            }
                            else -> {
                                // Limpiamos errores e iniciamos petición
                                errorGeneral = null
                                errorCorreo = null
                                errorContrasena = null
                                isLoading = true

                                coroutineScope.launch {
                                    try {
                                        val request = RegistroRequest(
                                            nombre = usuario,
                                            email = correo,
                                            password = contrasena
                                        )
                                        val response = RetrofitClient.apiService.registrarUsuario(request)

                                        if (response.isSuccessful) {
                                            isLoading = false
                                            onRegisterSuccess()
                                        } else {
                                            isLoading = false
                                            // 2. Si entra aquí, capturamos el HTTP 400 y procesamos el JSON de errores
                                            val errorBodyString = response.errorBody()?.string()
                                            if (!errorBodyString.isNullOrEmpty()) {
                                                try {
                                                    val validationErrors = Gson().fromJson(errorBodyString, ValidationErrorResponse::class.java)

                                                    // Buscamos los errores mapeados por las llaves exactas del backend ("Email" y "Password")
                                                    val emailErrors = validationErrors.errors?.get("Email")
                                                    val passwordErrors = validationErrors.errors?.get("Password")

                                                    if (!emailErrors.isNullOrEmpty()) {
                                                        errorCorreo = emailErrors.first() // Toma "El formato del email no es válido."
                                                    }
                                                    if (!passwordErrors.isNullOrEmpty()) {
                                                        errorContrasena = passwordErrors.first() // Toma "La contraseña debe tener al menos 6 caracteres."
                                                    }
                                                } catch (e: Exception) {
                                                    errorGeneral = "Error al procesar validaciones del servidor."
                                                }
                                            } else {
                                                errorGeneral = "Error desconocido del servidor (${response.code()})"
                                            }
                                        }
                                    } catch (e: Exception) {
                                        isLoading = false
                                        errorGeneral = "No se pudo conectar con el servidor. Verifica tu red."
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .width(240.dp)
                        .height(56.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorMarron),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                ) {
                    Text(
                        text = "CREAR CUENTA",
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
