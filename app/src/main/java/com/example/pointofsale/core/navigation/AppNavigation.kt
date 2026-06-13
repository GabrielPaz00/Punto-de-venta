package com.example.pointofsale.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pointofsale.core.components.MainLayout
import com.example.pointofsale.view.home.HomeView
import com.example.pointofsale.view.auth.LoginView
import com.example.pointofsale.view.launch.LaunchView
import com.example.pointofsale.view.profile.ProfileView
import com.example.pointofsale.viewmodel.home.HomeViewModel
import com.example.pointofsale.viewmodel.profile.ProfileViewModel

@Composable
fun App() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()
    val userState by homeViewModel.userState.collectAsState()

    MainLayout(
        userLevel = userState.userLevel,
        navController = navController
    ) { modifier ->
        NavHost(
            navController = navController,
            startDestination = "launch",
            modifier = modifier,
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
                        homeViewModel.fetchUserProfile() // Ensure profile is fresh
                        navController.navigate("home") {
                            popUpTo("launch") { inclusive = true }
                        }
                    }
                )
            }

            composable("login") {
                LoginView(
                    onLoginSuccess = { _ ->
                        homeViewModel.fetchUserProfile() // Load the new user profile
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("home") {
                HomeView(viewModel = homeViewModel, navController = navController)
            }

            // Placeholder routes for BottomNavBar items not yet implemented
            composable("pos") { /* Implement POSView */ }
            composable("products") { /* Implement ProductsView */ }
            composable("reports") { /* Implement ReportsView */ }
            composable("users") { /* Implement UsersView */ }
            composable("profile") {
                val profileViewModel: ProfileViewModel = viewModel()
                ProfileView(
                    viewModel = profileViewModel, 
                    navController = navController,
                    onLogout = {
                        homeViewModel.clearState() // Clear global state on logout
                    }
                )
            }
        }
    }
}
