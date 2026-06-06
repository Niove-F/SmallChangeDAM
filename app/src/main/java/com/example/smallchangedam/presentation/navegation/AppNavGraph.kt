package com.example.smallchangedam.presentation.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smallchangedam.presentation.admin.MainDashboard
import com.example.smallchangedam.presentation.admin.ServerScreen
import com.example.smallchangedam.presentation.home.HomeScreen
import com.example.smallchangedam.presentation.offers.PublishOfferScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ){
        composable ( "home" ){
                HomeScreen(navController = navController)
        }
        composable("publicarOferta") { PublishOfferScreen(navController) }
        composable("adminPanel") { MainDashboard(navController) }
    }

}