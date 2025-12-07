package com.activity.closetly.project_closedly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
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
                val authViewModel: AuthStateViewModel = hiltViewModel()

                val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

                val startDestination = if (isUserLoggedIn) {
                    Routes.WARDROBE
                } else {
                    Routes.LOGIN
                }

                NavGraph(startDestination = startDestination)
            }
        }
    }
}
