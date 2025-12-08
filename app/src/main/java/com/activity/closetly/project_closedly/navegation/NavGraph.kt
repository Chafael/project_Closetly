package com.activity.closetly.project_closedly.navegation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.activity.closetly.project_closedly.ui.login.LoginScreen
import com.activity.closetly.project_closedly.ui.screens.auth.RegisterScreen
import com.activity.closetly.project_closedly.ui.screens.profile.EditProfilePictureScreen
import com.activity.closetly.project_closedly.ui.screens.profile.ProfileScreen
import com.activity.closetly.project_closedly.ui.screens.wardrobe.WardrobeScreen
import com.activity.closetly.project_closedly.ui.viewmodel.LoginViewModel
import com.activity.closetly.project_closedly.ui.viewmodel.ProfileViewModel
import com.activity.closetly.project_closedly.ui.viewmodel.RegisterViewModelNew
import com.activity.closetly.project_closedly.ui.viewmodel.WardrobeViewModel

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
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.WARDROBE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }
        composable(Routes.REGISTER) {
            val registerViewModel: RegisterViewModelNew = hiltViewModel()
            RegisterScreen(
                registerViewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.WARDROBE) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.WARDROBE) {
            val wardrobeViewModel: WardrobeViewModel = hiltViewModel()
            val profileViewModel: ProfileViewModel = hiltViewModel()
            WardrobeScreen(
                wardrobeViewModel = wardrobeViewModel,
                profileViewModel = profileViewModel,
                onNavigateToUpload = {},
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
    const val WARDROBE = "wardrobe"
    const val PROFILE = "profile"
    const val EDIT_PROFILE_PICTURE = "edit_profile_picture"
}