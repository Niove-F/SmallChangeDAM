package com.example.smallchangedam.presentation.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.w3c.dom.Text
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)

data class Offer(
    val name: String,
    val actionType: String, // "Quiere vender" o "Quiere comprar"
    val amount: String,
    val tc: String, // Tipo de Cambio
    val timeAgo: String,
    val rating: String
)

@Composable
fun OfferCard(offer: Offer) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Fila Superior: Perfil, Nombre, Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(text = offer.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = offer.actionType, fontSize = 11.sp, color = Color.Gray)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFBC02D),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = offer.rating,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBC02D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fila Central: Monto y T.C. (Tipo de cambio)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = offer.amount,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFF66BB6A), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "T.C.: ${offer.tc}",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)

            // Fila Inferior: Tiempo transcurrido y Botón Intercambiar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = offer.timeAgo, fontSize = 11.sp, color = Color.Gray)

                Button(
                    onClick = { /* Acción de intercambiar */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF966F55)),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(text = "Intercambiar", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

// 3. Pantalla de Perfil Principal
@Composable
fun ProfileScreen(
    navController: NavController,
    userName: String,
    rating: String
) {
    val dummyOffers = listOf(
        Offer(userName, "Quiere vender", "¥ 46200", "0.024", "Hace 4 min", rating),
        Offer(userName, "Quiere vender", "$ 10512", "0.028", "Hace 8 min", rating),
        Offer(userName, "Quiere comprar", "S/ 150", "3.12", "Hace 9 min", rating)
    )

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // App Bar superior (Flecha Atrás e Historial)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate( "home" ) } ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
            }
            Text(text = "Perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección estática del perfil del usuario
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    // Avatar e Información de nombre
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color.Gray
                            )
                        }
                        Text(
                            text = userName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Caja de Rating
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Rating  ${rating}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            // Simulación de estrellas de calificación
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (it < rating.toFloat().roundToInt()) Color(0xFFFBC02D) else Color(0xFFBDBDBD),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Usuario desde info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Usuario desde:", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFCFD8DC), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "19 dic 2025", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Sección de la lista de ofertas activas con fondo gris
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .padding(vertical = 16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Ultimas ofertas activas:",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,

                        )

                        dummyOffers.forEach { offer ->
                            OfferCard(offer = offer)
                        }
                    }
                }
            }
        }
    }
}