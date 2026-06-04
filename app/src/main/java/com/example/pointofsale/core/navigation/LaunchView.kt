package com.example.pointofsale.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pointofsale.core.components.AppLogo
import com.example.pointofsale.core.theme.PointOfSaleTheme
import com.example.pointofsale.viewmodel.AuthState
import com.example.pointofsale.viewmodel.LaunchViewModel

@Composable
fun LaunchView(
    viewModel: LaunchViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: (userLevel: String) -> Unit
) {

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Unauthenticated -> {
                onNavigateToLogin()
            }
            is AuthState.Authenticated -> {
                onNavigateToHome(state.userLevel)
            }
            is AuthState.Loading -> {
                // Do nothing, wait for state transition
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppLogo(
            modifier = Modifier.size(120.dp),
            backgroundColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp,
            modifier = Modifier.size(48.dp)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun LaunchPreview() {
    PointOfSaleTheme(darkTheme = true) {
        LaunchView(
            onNavigateToLogin = {},
            onNavigateToHome = { _ -> }
        )
    }
}