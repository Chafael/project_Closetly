package com.activity.closetly.project_closedly.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.activity.closetly.project_closedly.ui.login.LoginScreen
import com.activity.closetly.project_closedly.ui.screens.auth.RegisterScreen
import com.activity.closetly.project_closedly.ui.screens.profile.ProfilePhotoScreen
import com.activity.closetly.project_closedly.ui.screens.profile.ProfileScreen
import com.activity.closetly.project_closedly.ui.screens.success.GarmentSuccessScreen
import com.activity.closetly.project_closedly.ui.screens.upload.UploadGarmentScreen
import com.activity.closetly.project_closedly.ui.screens.wardrobe.WardrobeScreen
import com.activity.closetly.project_closedly.ui.screens.welcome.WelcomeScreen
import com.activity.closetly.project_closedly.ui.screens.splash.SplashScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val WELCOME = "welcome"
    const val WARDROBE = "wardrobe"
    const val UPLOAD_GARMENT = "upload_garment"
    const val GARMENT_SUCCESS = "garment_success"
    const val PROFILE = "profile"
    const val PROFILE_PHOTO = "profile_photo"
    const val SPLASH = "splash"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH,
    isLoggedIn: Boolean = false
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    val targetDestination = if (isLoggedIn) Routes.WARDROBE else Routes.LOGIN
                    navController.navigate(targetDestination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.WARDROBE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onContinue = {
                    navController.navigate(Routes.WARDROBE) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.WARDROBE) {
            WardrobeScreen(
                onNavigateToUpload = {
                    navController.navigate(Routes.UPLOAD_GARMENT)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }

        composable(Routes.UPLOAD_GARMENT) {
            UploadGarmentScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onUploadSuccess = {
                    navController.navigate(Routes.GARMENT_SUCCESS) {
                        popUpTo(Routes.UPLOAD_GARMENT) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.GARMENT_SUCCESS) {
            GarmentSuccessScreen(
                onNavigateToWardrobe = {
                    navController.navigate(Routes.WARDROBE) {
                        popUpTo(Routes.GARMENT_SUCCESS) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToProfilePhoto = {
                    navController.navigate(Routes.PROFILE_PHOTO)
                }
            )
        }

        composable(Routes.PROFILE_PHOTO) {
            ProfilePhotoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPhotoUpdated = {
                }
            )
        }
    }
}