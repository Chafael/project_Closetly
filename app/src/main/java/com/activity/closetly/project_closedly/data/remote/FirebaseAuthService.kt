package com.activity.closetly.project_closedly.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirebaseAuthService"
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
    data object Loading : AuthResult<Nothing>()
}

@Singleton
class FirebaseAuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    init {
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "FirebaseAuthService inicializado")
        Log.d(TAG, "App Firebase: ${firebaseAuth.app.name}")
        Log.d(TAG, "Auth inicializado: ${firebaseAuth.currentUser != null}")
        Log.d(TAG, "Firestore inicializado: ${firestore.app.name}")

        // Si hay un usuario actual, mostrar info
        currentUser?.let { user ->
            Log.d(TAG, "Usuario actual: ${user.email}")
            Log.d(TAG, "UID: ${user.uid}")
        } ?: Log.d(TAG, "No hay usuario autenticado")

        Log.d(TAG, "════════════════════════════════════════")
    }
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser
    val isUserLoggedIn: Boolean
        get() = currentUser != null

    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        Log.d(TAG, "🔍 Iniciando observación de estado de auth")

        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            Log.d(TAG, "Estado de auth cambió: ${user?.email ?: "Sin usuario"}")
            trySend(user)
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            Log.d(TAG, "Deteniendo observación de auth")
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResult<FirebaseUser> {
        return try {
           Log.d(TAG, "Intentando login con: $email")

            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            result.user?.let { user ->
                Log.d(TAG, "Login exitoso")
                Log.d(TAG, "Usuario: ${user.email}")
                Log.d(TAG, "UID: ${user.uid}")
                AuthResult.Success(user)
            } ?: run {
                Log.e(TAG, "Error: Usuario nulo después de login")
                AuthResult.Error("Error al iniciar sesión")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en login: ${e.message}", e)
            AuthResult.Error(e.localizedMessage ?: "Error desconocido")
        }
    }
    suspend fun registerWithEmail(
        email: String,
        password: String,
        username: String
    ): AuthResult<FirebaseUser> {
        return try {
            Log.d(TAG, "Intentando registro con: $email")

            // Crear usuario en Firebase Authentication
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            result.user?.let { user ->
                Log.d(TAG, "Usuario creado en Auth")
                Log.d(TAG, "Email: ${user.email}")
                Log.d(TAG, "UID: ${user.uid}")

                // Guardar datos adicionales en Firestore
                val userData = hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "username" to username,
                    "createdAt" to System.currentTimeMillis()
                )

                Log.d(TAG, "💾 Guardando datos en Firestore...")

                // La colección "users" tendrá un documento con ID = UID del usuario
                firestore.collection("users")
                    .document(user.uid)
                    .set(userData)
                    .await()

                Log.d(TAG, "Datos guardados en Firestore")
                Log.d(TAG, "Ruta: users/${user.uid}")

                AuthResult.Success(user)
            } ?: run {
                Log.e(TAG, "Error: Usuario nulo después de registro")
                AuthResult.Error("Error al registrar usuario")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en registro: ${e.message}", e)
            firebaseAuth.currentUser?.delete()
            AuthResult.Error(e.localizedMessage ?: "Error desconocido")
        }
    }

    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            Log.d(TAG, "Enviando correo de recuperación a: $email")

            firebaseAuth.sendPasswordResetEmail(email).await()

            Log.d(TAG, "Correo de recuperación enviado")
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al enviar correo: ${e.message}", e)
            AuthResult.Error(e.localizedMessage ?: "Error al enviar correo")
        }
    }

    fun signOut() {
        val userEmail = currentUser?.email
        Log.d(TAG, "Cerrando sesión de: $userEmail")

        firebaseAuth.signOut()

        Log.d(TAG, "Sesión cerrada")
        Log.d(TAG, "Usuario actual: ${currentUser?.email ?: "Ninguno"}")

    }
}