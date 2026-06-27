package com.example.smallchangedam.presentation.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smallchangedam.presentation.admin.MainDashboard
import com.example.smallchangedam.presentation.auth.LoginScreen
import com.example.smallchangedam.presentation.auth.RegisterScreen
import com.example.smallchangedam.presentation.auth.VerificationScreen
import com.example.smallchangedam.presentation.home.HomeScreen
import com.example.smallchangedam.presentation.offers.PublishOfferScreen
import com.example.smallchangedam.presentation.perfil.ConfiguracionScreen
import com.example.smallchangedam.presentation.perfil.EditarPerfilScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // --- Autenticación ---
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("verification")
                },
                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }

        composable("verification") {
            VerificationScreen(
                onVerificationSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // --- Aplicación ---
        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("publicarOferta") { 
            PublishOfferScreen(navController) 
        }

        composable("adminPanel") {
            MainDashboard(navController) 
        }
        composable("editarPerfil"){
            EditarPerfilScreen(navController)
        }
        composable("configUser"){
            ConfiguracionScreen(navController)
        }
    }
}
