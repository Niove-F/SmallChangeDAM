package com.example.smallchangedam.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val route: String, val title: String, val icon: ImageVector) {
    Servidor("servidor", "Servidor", Default.Home),
    Inbox("inbox", "Inbox", Default.Email),
    Database("database", "Database", Icons.AutoMirrored.Filled.List),
    Users("users", "Users", Default.Person)
}

@Composable
fun Sidebar(navController: NavController, modifier: Modifier = Modifier) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(85.dp)
            .background(Color(0xFFEFEFEF)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {

            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Icon(
                imageVector = Default.Menu,
                contentDescription = "Menu",
                tint = Color.DarkGray,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Screen.entries.forEach { screen ->
            val isSelected = currentRoute == screen.route

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        color = if (isSelected) Color(0xFF966F55) else Color.Transparent,
                        shape = RoundedCornerShape(0.dp)
                    )
                    .clickable {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = screen.icon,
                    contentDescription = screen.title,
                    tint = if (isSelected) Color.White else Color.DarkGray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = screen.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun MainDashboard(navController: NavController) {
    val navController = rememberNavController()

    Row(modifier = Modifier.fillMaxSize()) {
        Sidebar(navController = navController)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Servidor.route
            ) {
                composable(Screen.Servidor.route) {
                    ServerScreen(navController)
                }
                composable(Screen.Inbox.route) {
                    InboxScreen(navController)
                }
                composable(Screen.Database.route) {
                    DatabaseLogsScreen(navController)
                }
                composable(Screen.Users.route) {
                    UsersScreen(navController)
                }
            }
        }
    }
}