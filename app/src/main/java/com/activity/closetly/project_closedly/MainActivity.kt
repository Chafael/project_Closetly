package com.activity.closetly.project_closedly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.activity.closetly.project_closedly.navegation.NavGraph
import com.activity.closetly.project_closedly.navegation.Routes
import com.activity.closetly.project_closedly.ui.theme.Project_ClosetlyTheme
import com.activity.closetly.project_closedly.ui.viewmodel.AuthStateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project_ClosetlyTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    val authStateViewModel: AuthStateViewModel = hiltViewModel()
    val isUserLoggedIn by authStateViewModel.isUserLoggedIn.collectAsState()

    val startDestination = if (isUserLoggedIn) Routes.WARDROBE else Routes.LOGIN

    NavGraph(
        navController = navController,
        startDestination = startDestination
    )
}