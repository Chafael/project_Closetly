package com.activity.closetly.project_closedly.navegation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.activity.closetly.project_closedly.ui.login.LoginScreen
import com.activity.closetly.project_closedly.ui.screens.auth.RegisterScreen
import com.activity.closetly.project_closedly.ui.screens.profile.ProfileScreen
import com.activity.closetly.project_closedly.ui.screens.wardrobe.WardrobeScreen
import com.activity.closetly.project_closedly.ui.screens.welcome.WelcomeScreen

private const val TAG = "NavGraph"

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val WELCOME = "welcome"
    const val WARDROBE = "wardrobe"
    const val UPLOAD_GARMENT = "upload_garment"
    const val PROFILE = "profile"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN
) {
    Log.d(TAG, "════════════════════════════════════════")
    Log.d(TAG, "NavGraph inicializado")
    Log.d(TAG, "Start destination: $startDestination")
    Log.d(TAG, "════════════════════════════════════════")

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            Log.d(TAG, "Mostrando LoginScreen")

            LoginScreen(
                onNavigateToRegister = {
                    Log.d(TAG, "Navegando a Register desde Login")
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    Log.d(TAG, "Login exitoso, navegando a Wardrobe")
                    navController.navigate(Routes.WARDROBE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            Log.d(TAG, "Mostrando RegisterScreen")

            RegisterScreen(
                onNavigateToLogin = {
                    Log.d(TAG, "Navegando a Login desde Register")
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    Log.d(TAG, "Registro exitoso, navegando a Welcome")
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.WELCOME) {
            Log.d(TAG, "Mostrando WelcomeScreen")

            WelcomeScreen(
                onContinue = {
                    Log.d(TAG, "Continuando a Wardrobe desde Welcome")
                    navController.navigate(Routes.WARDROBE) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.WARDROBE) {
            Log.d(TAG, "Mostrando WardrobeScreen")

            WardrobeScreen(
                onNavigateToUpload = {
                    Log.d(TAG, "Navegando a Upload desde Wardrobe")
                    navController.navigate(Routes.UPLOAD_GARMENT)
                },
                onNavigateToProfile = {
                    Log.d(TAG, "CLICK EN AVATAR - Navegando a Profile desde Wardrobe")
                    Log.d(TAG, "Current destination: ${navController.currentDestination?.route}")

                    // Línea problemática eliminada
                    // Log.d(TAG, "Back stack size: ${navController.backQueue.size}")

                    try {
                        navController.navigate(Routes.PROFILE)
                        Log.d(TAG, "Navegación a Profile ejecutada exitosamente")
                    } catch (e: Exception) {
                        Log.e(TAG, "ERROR al navegar a Profile: ${e.message}", e)
                    }
                }
            )
        }

        composable(Routes.UPLOAD_GARMENT) {
            Log.d(TAG, "Mostrando UploadGarment (placeholder)")

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pantalla de Subir Prenda",
                    fontSize = 20.sp
                )
            }
        }

        composable(Routes.PROFILE) {
            Log.d(TAG, "Mostrando ProfileScreen")
            // Línea problemática eliminada
            // Log.d(TAG, "Back stack al entrar a Profile: ${navController.backQueue.map { it.destination.route }}")

            ProfileScreen(
                onNavigateBack = {
                    Log.d(TAG, "Volviendo desde Profile")
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        Log.w(TAG, "No hay entrada anterior en el back stack")
                    }
                },
                onLogout = {
                    Log.d(TAG, "Logout ejecutado, navegando a Login")
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
