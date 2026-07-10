package com.example.smallchangedam.presentation.navegation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smallchangedam.data.SessionManager
import com.example.smallchangedam.presentation.admin.MainDashboard
import com.example.smallchangedam.presentation.auth.LoginScreen
import com.example.smallchangedam.presentation.auth.RegisterScreen
import com.example.smallchangedam.presentation.auth.VerificationScreen
import com.example.smallchangedam.presentation.home.HomeScreen
import com.example.smallchangedam.presentation.offers.DetallesOfertaOtroUsuario
import com.example.smallchangedam.ui.screens.PublishOfferScreen
import com.example.smallchangedam.presentation.perfil.ConfiguracionScreen
import com.example.smallchangedam.presentation.perfil.EditarPerfilScreen
import com.example.smallchangedam.presentation.perfil.ProfileScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "register"
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

        // CORRECCIÓN: Ajuste de parámetros para PublishOfferScreen
        composable("publicarOferta") {
            if (SessionManager.authToken.isNullOrBlank()) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            } else {
                PublishOfferScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onOfferPublished = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = "detallesOferta/{ofertaId}",
            arguments = listOf(navArgument("ofertaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val ofertaId = backStackEntry.arguments?.getInt("ofertaId") ?: 0
            DetallesOfertaOtroUsuario(navController = navController, ofertaId = ofertaId)
        }

        composable("adminPanel") {
            MainDashboard(navController)
        }

        composable("editarPerfil"){
            EditarPerfilScreen(navController)
        }

        composable(
            route = "perfil/{nombre}/{rating}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("rating") { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val nombre = navBackStackEntry.arguments?.getString("nombre").orEmpty()
            val rating = navBackStackEntry.arguments?.getString("rating").orEmpty()

            ProfileScreen(navController, nombre, rating)
        }

        composable("configUser"){
            ConfiguracionScreen(navController)
        }
    }
}