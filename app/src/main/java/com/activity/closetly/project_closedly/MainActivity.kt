package com.activity.closetly.project_closedly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.activity.closetly.project_closedly.navigation.NavGraph
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
                App(authViewModel)
            }
        }
    }
}

@Composable
fun App(authViewModel: AuthStateViewModel) {
    val navController = rememberNavController()
    NavGraph(
        navController = navController,
        authViewModel = authViewModel
    )
}
