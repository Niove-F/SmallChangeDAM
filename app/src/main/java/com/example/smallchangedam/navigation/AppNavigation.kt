package com.example.smallchangedam.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smallchangedam.presentation.auth.LoginScreen
import com.example.smallchangedam.presentation.auth.RegisterScreen
import com.example.smallchangedam.presentation.auth.VerificationScreen
import com.example.smallchangedam.presentation.home.HomeScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Verification : Screen("verification")
    object Home : Screen("home")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Después de registrar, lo mandamos a verificar la cuenta
                    navController.navigate(Screen.Verification.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.Verification.route) {
            VerificationScreen(
                onVerificationSuccess = {
                    // Después de verificar, lo mandamos al login
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
    }
}
