package com.example.pointofsale.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pointofsale.view.home.HomeView
import com.example.pointofsale.view.auth.LoginView
import com.example.pointofsale.view.launch.LaunchView
import com.example.pointofsale.viewmodel.home.HomeViewModel

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "launch",
        enterTransition = NavAnimations.enterTransition,
        exitTransition = NavAnimations.exitTransition,
        popEnterTransition = NavAnimations.popEnterTransition,
        popExitTransition = NavAnimations.popExitTransition
    ) {

        composable("launch") {
            LaunchView(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("launch") { inclusive = true }
                    }
                },
                onNavigateToHome = { _ ->
                    navController.navigate("home") {
                        popUpTo("launch") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginView(
                onLoginSuccess = { _ ->
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            val homeViewModel: HomeViewModel = viewModel()
            HomeView(viewModel = homeViewModel, navController = navController)
        }

        // Placeholder routes for BottomNavBar items not yet implemented
        composable("pos") { /* Implement POSView */ }
        composable("products") { /* Implement ProductsView */ }
        composable("reports") { /* Implement ReportsView */ }
        composable("users") { /* Implement UsersView */ }
        composable("profile") { /* Implement ProfileView */ }
    }
}
