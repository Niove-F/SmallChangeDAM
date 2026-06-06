package com.example.smallchangedam.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColorMarron = Color(0xFFB08968)
private val ColorGrisClaro = Color(0xFFD9D9D9)
private val ColorGrisInput = Color(0xFFE0E0E0)

@Composable
fun VerificationScreen(
    onVerificationSuccess: () -> Unit
) {
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
                    text = "Verifica tu\nCuenta",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 38.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.width(220.dp),
                    thickness = 0.5.dp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Por favor, ingrese el código que\nhemos enviado a e*****@g****.com",
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Cuadros de código (Simulados)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                CodeBox()
                CodeBox()
                CodeBox()
                CodeBox()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¿No recibió el código?",
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                text = "Reenviar el código",
                fontSize = 14.sp,
                color = Color(0xFF3F51B5),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { /* Acción reenvío */ }
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { onVerificationSuccess() },
                modifier = Modifier
                    .width(240.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorMarron),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "VERIFICAR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CodeBox() {
    Surface(
        modifier = Modifier.size(60.dp),
        shape = RoundedCornerShape(8.dp),
        color = ColorGrisInput,
        shadowElevation = 2.dp
    ) {
        // Por ahora vacío
    }
}

@Preview(showBackground = true)
@Composable
fun VerificationScreenPreview() {
    VerificationScreen(onVerificationSuccess = {})
}
