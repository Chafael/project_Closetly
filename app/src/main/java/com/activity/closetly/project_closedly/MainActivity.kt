package com.activity.closetly.project_closedly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.activity.closetly.project_closedly.navegation.NavGraph
import com.activity.closetly.project_closedly.navegation.Routes
import com.activity.closetly.project_closedly.ui.theme.Project_ClosetlyTheme
import com.activity.closetly.project_closedly.ui.viewmodel.AuthStateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthStateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project_ClosetlyTheme {
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                App(isLoggedIn)
            }
        }
    }
}

@Composable
fun App(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (isLoggedIn) Routes.HOME else Routes.LOGIN
    NavGraph(
        navController = navController,
        startDestination = startDestination
    )
}
