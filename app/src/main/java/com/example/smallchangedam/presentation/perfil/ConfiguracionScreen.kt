package com.example.smallchangedam.presentation.perfil

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    navController: NavController
) {
    val context = LocalContext.current // <-- 1. Obtenemos el contexto actual de Android

    var nombreUsuario by remember { mutableStateOf("Cargando...") }

    // 2. Rescatamos solo el nombre al entrar
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        nombreUsuario = sharedPreferences.getString("user_name", "Usuario") ?: "Usuario"
    }
    val ColorMarron = Color(0xFFB08968)
    val ColorGrisFondo = Color(0xFFE0E0E0)
    val ColorBlancoFondo = Color(0xFFF8F9FA)

    Scaffold(
        containerColor = ColorBlancoFondo,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColorMarron,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Text(
                        text = "Configuración",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sección de Perfil
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(
                        text = nombreUsuario,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp
                    )
                }

                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.clickable { navController.navigate("editarPerfil") }
                ) {
                    Surface(
                        modifier = Modifier.size(90.dp),
                        shape = CircleShape,
                        color = Color(0xFFE0E0E0)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(54.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        color = Color(0xFF757575),
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar perfil",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

            // Lista de Opciones con lambdas de acción personalizadas
            val configOptions = listOf(
                ConfigItem(Icons.Filled.Star, "Tema de la Aplicación") {},
                ConfigItem(Icons.Filled.Info, "Cambiar Idioma") {},
                ConfigItem(Icons.Filled.Settings, "Divisa Predeterminada") {},
                ConfigItem(Icons.Default.Notifications, "Preferencias de Notifs.") {},
                ConfigItem(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar Sesión") {
                    val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

                    sharedPreferences.edit()
                        .remove("auth_token")
                        .remove("user_name")
                        .apply()

                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                ConfigItem(Icons.Default.Delete, "Eliminar mi Cuenta") {}
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(configOptions) { item ->
                    ConfigOptionRow(item)
                }
            }
        }
    }
}

// 4. Modificamos el data class para añadirle la acción onClick
data class ConfigItem(
    val icon: ImageVector,
    val text: String,
    val onClick: () -> Unit
)

@Composable
fun ConfigOptionRow(item: ConfigItem) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { item.onClick() } // <-- Ejecuta la acción guardada en el item
                .padding(vertical = 18.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = Color.Black
            )
        }
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
    }
}