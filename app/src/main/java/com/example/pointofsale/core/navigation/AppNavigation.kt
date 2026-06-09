package com.example.pointofsale.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pointofsale.view.home.HomeView
import com.example.pointofsale.view.auth.LoginView
import com.example.pointofsale.view.launch.LaunchView
import com.example.pointofsale.viewmodel.home.HomeViewModel

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "launch") {

        composable("launch") {
            LaunchView(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("launch") { inclusive = true }
                    }
                },
                onNavigateToHome = { userLevel ->
                    navController.navigate("home/$userLevel") {
                        popUpTo("launch") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginView(
                onLoginSuccess = { userLevel ->
                    navController.navigate("home/$userLevel") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "home/{userLevel}",
            arguments = listOf(navArgument("userLevel") { type = NavType.StringType })
        ) { backStackEntry ->
            val userLevel = backStackEntry.arguments?.getString("userLevel") ?: "user"
            val homeViewModel: HomeViewModel = viewModel()
            HomeView(viewModel = homeViewModel, userLevel = userLevel)
        }
    }
}