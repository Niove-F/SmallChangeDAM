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
import com.example.smallchangedam.presentation.components.CustomInputField

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
                .height(240.dp)
                .background(ColorGrisClaro, shape = curveShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 10.dp)
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
            CustomInputField(value = nombres, onValueChange = { nombres = it }, placeholder = "Nombres")
            CustomInputField(value = apellidos, onValueChange = { apellidos = it }, placeholder = "Apellidos")
            
            Column {
                CustomInputField(value = usuario, onValueChange = { usuario = it }, placeholder = "Ingrese su Usuario")
                Text(
                    text = "Su usuario será visible en todos lados.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                )
            }

            CustomInputField(value = correo, onValueChange = { correo = it }, placeholder = "Correo Electrónico")
            CustomInputField(value = contrasena, onValueChange = { contrasena = it }, placeholder = "Ingresar Contraseña", isPassword = true)
            CustomInputField(value = confirmarContrasena, onValueChange = { confirmarContrasena = it }, placeholder = "Confirmar Contraseña", isPassword = true)

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
                onClick = { 
                    when {
                        nombres.isBlank() || apellidos.isBlank() || usuario.isBlank() || correo.isBlank() || contrasena.isBlank() -> {
                            errorText = "Todos los campos son obligatorios"
                        }
                        !correo.contains("@") || !correo.contains(".") -> {
                            errorText = "El correo electrónico no es válido"
                        }
                        contrasena != confirmarContrasena -> {
                            errorText = "Las contraseñas no coinciden"
                        }
                        contrasena.length < 6 -> {
                            errorText = "La contraseña debe tener al menos 6 caracteres"
                        }
                        else -> {
                            errorText = null
                            onRegisterSuccess()
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
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegisterSuccess = {}, onNavigateToLogin = {})
}
