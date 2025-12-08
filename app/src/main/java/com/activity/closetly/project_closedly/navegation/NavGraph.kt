package com.activity.closetly.project_closedly.navegation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.activity.closetly.project_closedly.ui.login.LoginScreen
import com.activity.closetly.project_closedly.ui.screens.home.HomeScreen
import com.activity.closetly.project_closedly.ui.screens.profile.EditProfilePictureScreen
import com.activity.closetly.project_closedly.ui.screens.profile.ProfileScreen
import com.activity.closetly.project_closedly.ui.screens.auth.RegisterScreen
import com.activity.closetly.project_closedly.ui.viewmodel.AuthStateViewModel
import com.activity.closetly.project_closedly.ui.viewmodel.ProfileViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            val authViewModel: AuthStateViewModel = hiltViewModel()
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }
        composable(Routes.REGISTER) {
            val authViewModel: AuthStateViewModel = hiltViewModel()
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }
        composable(Routes.PROFILE) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                profileViewModel = profileViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToEditPicture = {
                    navController.navigate(Routes.EDIT_PROFILE_PICTURE)
                }
            )
        }
        composable(Routes.EDIT_PROFILE_PICTURE) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            EditProfilePictureScreen(
                profileViewModel = profileViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val EDIT_PROFILE_PICTURE = "edit_profile_picture"
}

