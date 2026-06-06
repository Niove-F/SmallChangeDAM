package com.example.smallchangedam.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

enum class LogType(val label: String, val backgroundColor: Color, val textColor: Color) {
    SELECT("SELECT", Color(0xFFE8F5E9), Color(0xFF2E7D32)),
    INSERT("INSERT", Color(0xFFE3F2FD), Color(0xFF1565C0)),
    UPDATE("UPDATE", Color(0xFFFFF3E0), Color(0xFFEF6C00))
}

data class DatabaseLog(
    val id: String,
    val query: String,
    val type: LogType
)

@Composable
fun LogItem(log: DatabaseLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: #${log.id}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Box(
                    modifier = Modifier
                        .background(color = log.type.backgroundColor, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = log.type.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = log.type.textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = log.query,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF2196F3)
            )
        }
    }
}

// Logs BD
@Composable
fun DatabaseLogsScreen(navController: NavController) {
    val logs = listOf(
        DatabaseLog("5501", "SELECT * FROM ofertas WHERE estado='OPEN'", LogType.SELECT),
        DatabaseLog("5502", "INSERT INTO transacciones (id, monto) VALUES ...", LogType.INSERT),
        DatabaseLog("5503", "UPDATE usuarios SET verificado=true WHERE ...", LogType.UPDATE)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Logs de Base de Datos",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(logs) { log ->
                LogItem(log = log)
            }
        }
    }
}